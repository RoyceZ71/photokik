package com.mocha.photokik.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.mocha.photokik.data.model.Photo
import com.mocha.photokik.data.model.PhotoCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIPhotoAnalyzer @Inject constructor() {
    
    companion object {
        private const val TAG = "AIPhotoAnalyzer"
        
        // Keywords for document detection
        private val DOCUMENT_KEYWORDS = setOf(
            "text", "document", "paper", "receipt", "bill", "invoice", 
            "letter", "form", "contract", "screenshot", "page"
        )
        
        // Keywords for memory detection
        private val MEMORY_KEYWORDS = setOf(
            "person", "people", "face", "family", "friends", "party",
            "vacation", "travel", "celebration", "wedding", "birthday"
        )
    }
    
    /**
     * Analyze photo using on-device AI techniques
     */
    suspend fun analyzePhoto(photo: Photo): Result<Photo> = withContext(Dispatchers.IO) {
        try {
            val file = File(photo.filePath)
            if (!file.exists()) {
                return@withContext Result.failure(Exception("Photo file not found"))
            }
            
            val bitmap = BitmapFactory.decodeFile(photo.filePath)
                ?: return@withContext Result.failure(Exception("Failed to decode image"))
            
            // Perform various AI analyses
            val category = detectCategory(bitmap, photo)
            val tags = generateTags(bitmap, photo)
            val blurScore = calculateAdvancedBlurScore(bitmap)
            
            bitmap.recycle()
            
            val analyzedPhoto = photo.copy(
                category = category,
                aiTags = tags.joinToString(","),
                blurScore = blurScore
            )
            
            Result.success(analyzedPhoto)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error analyzing photo", e)
            Result.failure(e)
        }
    }
    
    /**
     * Detect photo category using various heuristics
     */
    private fun detectCategory(bitmap: Bitmap, photo: Photo): PhotoCategory {
        // Check blur score first
        val blurScore = calculateAdvancedBlurScore(bitmap)
        if (blurScore > 0.7f) {
            return PhotoCategory.BLURRY
        }
        
        // Analyze filename for hints
        val filename = photo.filename.lowercase()
        when {
            filename.contains("screenshot") || 
            filename.contains("img_") && hasHighContrast(bitmap) -> {
                return PhotoCategory.DOCUMENTS
            }
            
            filename.contains("camera") ||
            filename.contains("photo") ||
            filename.contains("pic") -> {
                return PhotoCategory.MEMORIES
            }
        }
        
        // Analyze image characteristics
        return when {
            isLikelyDocument(bitmap) -> PhotoCategory.DOCUMENTS
            isLikelyMemory(bitmap) -> PhotoCategory.MEMORIES
            else -> PhotoCategory.UNCATEGORIZED
        }
    }
    
    /**
     * Generate AI tags for the photo
     */
    private fun generateTags(bitmap: Bitmap, photo: Photo): List<String> {
        val tags = mutableListOf<String>()
        
        // Add basic characteristics
        if (bitmap.width > bitmap.height) {
            tags.add("landscape")
        } else {
            tags.add("portrait")
        }
        
        // Add size category
        val megapixels = (bitmap.width * bitmap.height) / 1_000_000f
        when {
            megapixels > 8 -> tags.add("high_resolution")
            megapixels > 2 -> tags.add("medium_resolution")
            else -> tags.add("low_resolution")
        }
        
        // Add color analysis
        val colorAnalysis = analyzeColors(bitmap)
        tags.addAll(colorAnalysis)
        
        // Add filename-based tags
        val filename = photo.filename.lowercase()
        when {
            filename.contains("selfie") -> tags.add("selfie")
            filename.contains("group") -> tags.add("group_photo")
            filename.contains("food") -> tags.add("food")
            filename.contains("nature") -> tags.add("nature")
            filename.contains("city") -> tags.add("urban")
        }
        
        return tags.distinct().take(10) // Limit to 10 tags
    }
    
    /**
     * Advanced blur detection using multiple methods
     */
    private fun calculateAdvancedBlurScore(bitmap: Bitmap): Float {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true)
        
        try {
            val pixels = IntArray(resizedBitmap.width * resizedBitmap.height)
            resizedBitmap.getPixels(pixels, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)
            
            // Convert to grayscale
            val grayscale = pixels.map { pixel ->
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                (0.299 * r + 0.587 * g + 0.114 * b).toInt()
            }
            
            // Calculate Laplacian variance
            val laplacianVariance = calculateLaplacianVariance(grayscale, resizedBitmap.width, resizedBitmap.height)
            
            // Calculate edge density
            val edgeDensity = calculateEdgeDensity(grayscale, resizedBitmap.width, resizedBitmap.height)
            
            // Combine metrics (normalized to 0-1 where 1 = very blurry)
            val normalizedLaplacian = 1.0f - (laplacianVariance / 10000f).coerceIn(0f, 1f)
            val normalizedEdges = 1.0f - (edgeDensity / 0.5f).coerceIn(0f, 1f)
            
            return (normalizedLaplacian * 0.7f + normalizedEdges * 0.3f).coerceIn(0f, 1f)
            
        } finally {
            if (resizedBitmap != bitmap) {
                resizedBitmap.recycle()
            }
        }
    }
    
    /**
     * Calculate Laplacian variance for blur detection
     */
    private fun calculateLaplacianVariance(grayscale: List<Int>, width: Int, height: Int): Float {
        var sum = 0.0
        var sumSq = 0.0
        var count = 0
        
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val idx = y * width + x
                val laplacian = -4 * grayscale[idx] +
                        grayscale[idx - 1] + grayscale[idx + 1] +
                        grayscale[idx - width] + grayscale[idx + width]
                
                sum += laplacian
                sumSq += laplacian * laplacian
                count++
            }
        }
        
        if (count == 0) return 0f
        
        val mean = sum / count
        return ((sumSq / count) - (mean * mean)).toFloat()
    }
    
    /**
     * Calculate edge density
     */
    private fun calculateEdgeDensity(grayscale: List<Int>, width: Int, height: Int): Float {
        var edgePixels = 0
        var totalPixels = 0
        
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val idx = y * width + x
                
                // Sobel edge detection
                val gx = -grayscale[idx - width - 1] - 2 * grayscale[idx - 1] - grayscale[idx + width - 1] +
                        grayscale[idx - width + 1] + 2 * grayscale[idx + 1] + grayscale[idx + width + 1]
                
                val gy = -grayscale[idx - width - 1] - 2 * grayscale[idx - width] - grayscale[idx - width + 1] +
                        grayscale[idx + width - 1] + 2 * grayscale[idx + width] + grayscale[idx + width + 1]
                
                val magnitude = kotlin.math.sqrt((gx * gx + gy * gy).toDouble())
                
                if (magnitude > 30) { // Edge threshold
                    edgePixels++
                }
                totalPixels++
            }
        }
        
        return if (totalPixels > 0) edgePixels.toFloat() / totalPixels else 0f
    }
    
    /**
     * Check if image is likely a document
     */
    private fun isLikelyDocument(bitmap: Bitmap): Boolean {
        // High contrast and text-like characteristics
        return hasHighContrast(bitmap) && hasRegularPatterns(bitmap)
    }
    
    /**
     * Check if image is likely a memory/photo
     */
    private fun isLikelyMemory(bitmap: Bitmap): Boolean {
        // Natural color distribution and moderate contrast
        return hasNaturalColors(bitmap) && !hasHighContrast(bitmap)
    }
    
    /**
     * Check if image has high contrast (typical of documents/screenshots)
     */
    private fun hasHighContrast(bitmap: Bitmap): Boolean {
        val sample = Bitmap.createScaledBitmap(bitmap, 50, 50, true)
        val pixels = IntArray(sample.width * sample.height)
        sample.getPixels(pixels, 0, sample.width, 0, 0, sample.width, sample.height)
        
        val grayscale = pixels.map { pixel ->
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            (0.299 * r + 0.587 * g + 0.114 * b).toInt()
        }
        
        val min = grayscale.minOrNull() ?: 0
        val max = grayscale.maxOrNull() ?: 255
        val contrast = (max - min).toFloat() / 255f
        
        if (sample != bitmap) sample.recycle()
        
        return contrast > 0.7f
    }
    
    /**
     * Check for regular patterns (typical of documents)
     */
    private fun hasRegularPatterns(bitmap: Bitmap): Boolean {
        // Simplified check - in real implementation would use more sophisticated pattern detection
        return bitmap.width.toFloat() / bitmap.height > 1.2f // Likely landscape document
    }
    
    /**
     * Check for natural color distribution
     */
    private fun hasNaturalColors(bitmap: Bitmap): Boolean {
        val sample = Bitmap.createScaledBitmap(bitmap, 50, 50, true)
        val pixels = IntArray(sample.width * sample.height)
        sample.getPixels(pixels, 0, sample.width, 0, 0, sample.width, sample.height)
        
        var colorVariance = 0f
        pixels.forEach { pixel ->
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            
            // Calculate color variance from grayscale
            val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
            val variance = ((r - gray) * (r - gray) + (g - gray) * (g - gray) + (b - gray) * (b - gray)) / 3f
            colorVariance += variance
        }
        
        colorVariance /= pixels.size
        
        if (sample != bitmap) sample.recycle()
        
        return colorVariance > 100f // Has sufficient color variation
    }
    
    /**
     * Analyze dominant colors in the image
     */
    private fun analyzeColors(bitmap: Bitmap): List<String> {
        val sample = Bitmap.createScaledBitmap(bitmap, 30, 30, true)
        val pixels = IntArray(sample.width * sample.height)
        sample.getPixels(pixels, 0, sample.width, 0, 0, sample.width, sample.height)
        
        var redSum = 0L
        var greenSum = 0L
        var blueSum = 0L
        
        pixels.forEach { pixel ->
            redSum += (pixel shr 16) and 0xFF
            greenSum += (pixel shr 8) and 0xFF
            blueSum += pixel and 0xFF
        }
        
        val avgRed = redSum / pixels.size
        val avgGreen = greenSum / pixels.size
        val avgBlue = blueSum / pixels.size
        
        val tags = mutableListOf<String>()
        
        // Determine dominant color
        when {
            avgRed > avgGreen && avgRed > avgBlue -> tags.add("red_dominant")
            avgGreen > avgRed && avgGreen > avgBlue -> tags.add("green_dominant")
            avgBlue > avgRed && avgBlue > avgGreen -> tags.add("blue_dominant")
        }
        
        // Check for black and white
        val brightness = (avgRed + avgGreen + avgBlue) / 3
        when {
            brightness < 60 -> tags.add("dark")
            brightness > 200 -> tags.add("bright")
            else -> tags.add("medium_brightness")
        }
        
        // Check for monochrome
        val colorRange = maxOf(avgRed, avgGreen, avgBlue) - minOf(avgRed, avgGreen, avgBlue)
        if (colorRange < 30) {
            tags.add("monochrome")
        } else {
            tags.add("colorful")
        }
        
        if (sample != bitmap) sample.recycle()
        
        return tags
    }
    
    /**
     * Batch analyze multiple photos
     */
    suspend fun batchAnalyze(photos: List<Photo>): List<Photo> = withContext(Dispatchers.IO) {
        photos.map { photo ->
            analyzePhoto(photo).getOrElse { photo }
        }
    }
    
    /**
     * Check if two photos are visually similar
     */
    suspend fun arePhotosSimilar(photo1: Photo, photo2: Photo, threshold: Float = 0.8f): Boolean = 
        withContext(Dispatchers.IO) {
        try {
            val bitmap1 = BitmapFactory.decodeFile(photo1.filePath) ?: return@withContext false
            val bitmap2 = BitmapFactory.decodeFile(photo2.filePath) ?: return@withContext false
            
            val similarity = calculateImageSimilarity(bitmap1, bitmap2)
            
            bitmap1.recycle()
            bitmap2.recycle()
            
            similarity > threshold
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Calculate similarity between two images using histogram comparison
     */
    private fun calculateImageSimilarity(bitmap1: Bitmap, bitmap2: Bitmap): Float {
        val size = 32
        val sample1 = Bitmap.createScaledBitmap(bitmap1, size, size, true)
        val sample2 = Bitmap.createScaledBitmap(bitmap2, size, size, true)
        
        val pixels1 = IntArray(size * size)
        val pixels2 = IntArray(size * size)
        
        sample1.getPixels(pixels1, 0, size, 0, 0, size, size)
        sample2.getPixels(pixels2, 0, size, 0, 0, size, size)
        
        // Create color histograms
        val hist1 = IntArray(256)
        val hist2 = IntArray(256)
        
        pixels1.forEach { pixel ->
            val gray = ((pixel shr 16) and 0xFF) * 0.299 + 
                      ((pixel shr 8) and 0xFF) * 0.587 + 
                      (pixel and 0xFF) * 0.114
            hist1[gray.toInt().coerceIn(0, 255)]++
        }
        
        pixels2.forEach { pixel ->
            val gray = ((pixel shr 16) and 0xFF) * 0.299 + 
                      ((pixel shr 8) and 0xFF) * 0.587 + 
                      (pixel and 0xFF) * 0.114
            hist2[gray.toInt().coerceIn(0, 255)]++
        }
        
        // Calculate histogram correlation
        var correlation = 0.0
        var sum1 = 0.0
        var sum2 = 0.0
        var sum1Sq = 0.0
        var sum2Sq = 0.0
        
        for (i in hist1.indices) {
            val h1 = hist1[i].toDouble()
            val h2 = hist2[i].toDouble()
            
            correlation += h1 * h2
            sum1 += h1
            sum2 += h2
            sum1Sq += h1 * h1
            sum2Sq += h2 * h2
        }
        
        val n = hist1.size.toDouble()
        val numerator = n * correlation - sum1 * sum2
        val denominator = kotlin.math.sqrt((n * sum1Sq - sum1 * sum1) * (n * sum2Sq - sum2 * sum2))
        
        if (sample1 != bitmap1) sample1.recycle()
        if (sample2 != bitmap2) sample2.recycle()
        
        return if (denominator != 0.0) {
            (numerator / denominator).toFloat().coerceIn(-1f, 1f)
        } else {
            0f
        }
    }
}
