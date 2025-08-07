package com.mocha.photokik.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.mocha.photokik.data.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.sqrt

@Singleton
class DuplicateDetector @Inject constructor() {
    
    companion object {
        private const val TAG = "DuplicateDetector"
        private const val SIMILARITY_THRESHOLD = 0.85f
        private const val HASH_SIZE = 8 // For perceptual hashing
    }
    
    data class DuplicateGroup(
        val originalPhoto: Photo,
        val duplicates: List<Photo>,
        val similarityScores: Map<Long, Float>
    )
    
    /**
     * Find duplicate photos in a list using multiple detection methods
     */
    suspend fun findDuplicates(photos: List<Photo>): List<DuplicateGroup> = withContext(Dispatchers.IO) {
        if (photos.size < 2) return@withContext emptyList()
        
        val duplicateGroups = mutableListOf<DuplicateGroup>()
        val processedPhotos = mutableSetOf<Long>()
        
        // First pass: Exact duplicates by file hash
        val exactDuplicates = findExactDuplicates(photos)
        exactDuplicates.forEach { group ->
            if (group.duplicates.isNotEmpty()) {
                duplicateGroups.add(group)
                processedPhotos.add(group.originalPhoto.id)
                processedPhotos.addAll(group.duplicates.map { it.id })
            }
        }
        
        // Second pass: Similar duplicates by perceptual hash
        val remainingPhotos = photos.filter { !processedPhotos.contains(it.id) }
        val similarDuplicates = findSimilarDuplicates(remainingPhotos)
        duplicateGroups.addAll(similarDuplicates)
        
        Log.d(TAG, "Found ${duplicateGroups.size} duplicate groups")
        duplicateGroups
    }
    
    /**
     * Find exact duplicates using file hash comparison
     */
    private suspend fun findExactDuplicates(photos: List<Photo>): List<DuplicateGroup> = 
        withContext(Dispatchers.IO) {
        val hashToPhotos = mutableMapOf<String, MutableList<Photo>>()
        
        // Calculate file hashes
        photos.forEach { photo ->
            try {
                val hash = calculateFileHash(photo.filePath)
                if (hash != null) {
                    hashToPhotos.getOrPut(hash) { mutableListOf() }.add(photo)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to calculate hash for ${photo.filename}", e)
            }
        }
        
        // Create duplicate groups
        hashToPhotos.values
            .filter { it.size > 1 }
            .map { duplicateList ->
                val sortedList = duplicateList.sortedBy { it.createdAt }
                val original = sortedList.first()
                val duplicates = sortedList.drop(1)
                val similarities = duplicates.associate { it.id to 1.0f } // Exact match
                
                DuplicateGroup(original, duplicates, similarities)
            }
    }
    
    /**
     * Find similar duplicates using perceptual hashing
     */
    private suspend fun findSimilarDuplicates(photos: List<Photo>): List<DuplicateGroup> = 
        withContext(Dispatchers.IO) {
        if (photos.size < 2) return@withContext emptyList()
        
        val duplicateGroups = mutableListOf<DuplicateGroup>()
        val processedPhotos = mutableSetOf<Long>()
        
        // Calculate perceptual hashes for all photos
        val photoHashes = photos.mapNotNull { photo ->
            try {
                val hash = calculatePerceptualHash(photo.filePath)
                if (hash != null) photo to hash else null
            } catch (e: Exception) {
                Log.w(TAG, "Failed to calculate perceptual hash for ${photo.filename}", e)
                null
            }
        }.toMap()
        
        // Find similar photos
        photos.forEach { photo1 ->
            if (processedPhotos.contains(photo1.id)) return@forEach
            
            val hash1 = photoHashes[photo1] ?: return@forEach
            val similarPhotos = mutableListOf<Photo>()
            val similarities = mutableMapOf<Long, Float>()
            
            photos.forEach { photo2 ->
                if (photo1.id != photo2.id && !processedPhotos.contains(photo2.id)) {
                    val hash2 = photoHashes[photo2]
                    if (hash2 != null) {
                        val similarity = calculateHashSimilarity(hash1, hash2)
                        if (similarity > SIMILARITY_THRESHOLD) {
                            similarPhotos.add(photo2)
                            similarities[photo2.id] = similarity
                        }
                    }
                }
            }
            
            if (similarPhotos.isNotEmpty()) {
                // Sort by creation date to determine original
                val allPhotos = listOf(photo1) + similarPhotos
                val sortedPhotos = allPhotos.sortedBy { it.createdAt }
                val original = sortedPhotos.first()
                val duplicates = sortedPhotos.drop(1)
                
                duplicateGroups.add(DuplicateGroup(original, duplicates, similarities))
                
                // Mark all as processed
                processedPhotos.add(original.id)
                processedPhotos.addAll(duplicates.map { it.id })
            }
        }
        
        duplicateGroups
    }
    
    /**
     * Calculate MD5 hash of file content
     */
    private fun calculateFileHash(filePath: String): String? {
        return try {
            val file = File(filePath)
            if (!file.exists()) return null
            
            val digest = MessageDigest.getInstance("MD5")
            val buffer = ByteArray(8192)
            
            file.inputStream().use { inputStream ->
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating file hash", e)
            null
        }
    }
    
    /**
     * Calculate perceptual hash using difference hash algorithm
     */
    private fun calculatePerceptualHash(filePath: String): String? {
        return try {
            val bitmap = BitmapFactory.decodeFile(filePath) ?: return null
            val hash = calculateDHash(bitmap)
            bitmap.recycle()
            hash
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating perceptual hash", e)
            null
        }
    }
    
    /**
     * Calculate difference hash (dHash) for perceptual comparison
     */
    private fun calculateDHash(bitmap: Bitmap): String {
        // Resize to (HASH_SIZE + 1) x HASH_SIZE
        val resized = Bitmap.createScaledBitmap(bitmap, HASH_SIZE + 1, HASH_SIZE, true)
        
        // Convert to grayscale and calculate differences
        val hash = StringBuilder()
        
        for (y in 0 until HASH_SIZE) {
            for (x in 0 until HASH_SIZE) {
                val pixel1 = resized.getPixel(x, y)
                val pixel2 = resized.getPixel(x + 1, y)
                
                val gray1 = getGrayscale(pixel1)
                val gray2 = getGrayscale(pixel2)
                
                hash.append(if (gray1 > gray2) "1" else "0")
            }
        }
        
        if (resized != bitmap) {
            resized.recycle()
        }
        
        return hash.toString()
    }
    
    /**
     * Convert RGB pixel to grayscale
     */
    private fun getGrayscale(pixel: Int): Int {
        val r = (pixel shr 16) and 0xFF
        val g = (pixel shr 8) and 0xFF
        val b = pixel and 0xFF
        return (0.299 * r + 0.587 * g + 0.114 * b).toInt()
    }
    
    /**
     * Calculate similarity between two perceptual hashes
     */
    private fun calculateHashSimilarity(hash1: String, hash2: String): Float {
        if (hash1.length != hash2.length) return 0f
        
        var differences = 0
        for (i in hash1.indices) {
            if (hash1[i] != hash2[i]) {
                differences++
            }
        }
        
        val hammingDistance = differences
        val maxDistance = hash1.length
        
        return 1f - (hammingDistance.toFloat() / maxDistance)
    }
    
    /**
     * Check if two photos are duplicates
     */
    suspend fun areDuplicates(photo1: Photo, photo2: Photo): Boolean = withContext(Dispatchers.IO) {
        try {
            // First check exact match
            val hash1 = calculateFileHash(photo1.filePath)
            val hash2 = calculateFileHash(photo2.filePath)
            
            if (hash1 != null && hash2 != null && hash1 == hash2) {
                return@withContext true
            }
            
            // Then check perceptual similarity
            val pHash1 = calculatePerceptualHash(photo1.filePath)
            val pHash2 = calculatePerceptualHash(photo2.filePath)
            
            if (pHash1 != null && pHash2 != null) {
                val similarity = calculateHashSimilarity(pHash1, pHash2)
                return@withContext similarity > SIMILARITY_THRESHOLD
            }
            
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error comparing photos for duplicates", e)
            false
        }
    }
    
    /**
     * Find duplicates of a specific photo in a list
     */
    suspend fun findDuplicatesOf(targetPhoto: Photo, photos: List<Photo>): List<Photo> = 
        withContext(Dispatchers.IO) {
        val duplicates = mutableListOf<Photo>()
        
        photos.forEach { photo ->
            if (photo.id != targetPhoto.id && areDuplicates(targetPhoto, photo)) {
                duplicates.add(photo)
            }
        }
        
        duplicates
    }
    
    /**
     * Calculate image similarity using histogram comparison
     */
    suspend fun calculateImageSimilarity(photo1: Photo, photo2: Photo): Float = withContext(Dispatchers.IO) {
        try {
            val bitmap1 = BitmapFactory.decodeFile(photo1.filePath) ?: return@withContext 0f
            val bitmap2 = BitmapFactory.decodeFile(photo2.filePath) ?: return@withContext 0f
            
            val similarity = compareHistograms(bitmap1, bitmap2)
            
            bitmap1.recycle()
            bitmap2.recycle()
            
            similarity
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating image similarity", e)
            0f
        }
    }
    
    /**
     * Compare color histograms of two bitmaps
     */
    private fun compareHistograms(bitmap1: Bitmap, bitmap2: Bitmap): Float {
        val size = 32
        val sample1 = Bitmap.createScaledBitmap(bitmap1, size, size, true)
        val sample2 = Bitmap.createScaledBitmap(bitmap2, size, size, true)
        
        // Create RGB histograms
        val hist1R = IntArray(256)
        val hist1G = IntArray(256)
        val hist1B = IntArray(256)
        val hist2R = IntArray(256)
        val hist2G = IntArray(256)
        val hist2B = IntArray(256)
        
        // Populate histograms
        for (y in 0 until size) {
            for (x in 0 until size) {
                val pixel1 = sample1.getPixel(x, y)
                val pixel2 = sample2.getPixel(x, y)
                
                hist1R[(pixel1 shr 16) and 0xFF]++
                hist1G[(pixel1 shr 8) and 0xFF]++
                hist1B[pixel1 and 0xFF]++
                
                hist2R[(pixel2 shr 16) and 0xFF]++
                hist2G[(pixel2 shr 8) and 0xFF]++
                hist2B[pixel2 and 0xFF]++
            }
        }
        
        // Calculate correlation for each channel
        val corrR = calculateCorrelation(hist1R, hist2R)
        val corrG = calculateCorrelation(hist1G, hist2G)
        val corrB = calculateCorrelation(hist1B, hist2B)
        
        if (sample1 != bitmap1) sample1.recycle()
        if (sample2 != bitmap2) sample2.recycle()
        
        // Average correlation across channels
        return (corrR + corrG + corrB) / 3f
    }
    
    /**
     * Calculate correlation between two histograms
     */
    private fun calculateCorrelation(hist1: IntArray, hist2: IntArray): Float {
        var sum1 = 0.0
        var sum2 = 0.0
        var sum1Sq = 0.0
        var sum2Sq = 0.0
        var productSum = 0.0
        
        for (i in hist1.indices) {
            val x = hist1[i].toDouble()
            val y = hist2[i].toDouble()
            
            sum1 += x
            sum2 += y
            sum1Sq += x * x
            sum2Sq += y * y
            productSum += x * y
        }
        
        val n = hist1.size.toDouble()
        val numerator = n * productSum - sum1 * sum2
        val denominator = sqrt((n * sum1Sq - sum1 * sum1) * (n * sum2Sq - sum2 * sum2))
        
        return if (denominator != 0.0) {
            (numerator / denominator).toFloat().coerceIn(-1f, 1f)
        } else {
            0f
        }
    }
    
    /**
     * Get statistics about duplicates in a photo collection
     */
    suspend fun getDuplicateStats(photos: List<Photo>): DuplicateStats = withContext(Dispatchers.IO) {
        val duplicateGroups = findDuplicates(photos)
        
        val totalDuplicates = duplicateGroups.sumOf { it.duplicates.size }
        val duplicateFiles = duplicateGroups.flatMap { it.duplicates }
        val spaceWasted = duplicateFiles.sumOf { it.fileSize ?: 0L }
        
        DuplicateStats(
            totalGroups = duplicateGroups.size,
            totalDuplicates = totalDuplicates,
            spaceWasted = spaceWasted,
            duplicateGroups = duplicateGroups
        )
    }
    
    data class DuplicateStats(
        val totalGroups: Int,
        val totalDuplicates: Int,
        val spaceWasted: Long,
        val duplicateGroups: List<DuplicateGroup>
    )
}
