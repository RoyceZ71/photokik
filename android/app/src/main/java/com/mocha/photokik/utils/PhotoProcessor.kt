package com.mocha.photokik.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import com.mocha.photokik.data.model.Photo
import com.mocha.photokik.data.model.PhotoCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.sqrt

@Singleton
class PhotoProcessor @Inject constructor() {
    
    companion object {
        private const val MAX_IMAGE_SIZE = 2048
        private const val JPEG_QUALITY = 85
        private const val BLUR_THRESHOLD = 100.0
    }
    
    /**
     * Process a photo from external storage and create optimized copy
     */
    suspend fun processPhoto(
        context: Context,
        sourceUri: Uri,
        filename: String? = null
    ): Result<Photo> = withContext(Dispatchers.IO) {
        try {
            val processedFile = createProcessedPhoto(context, sourceUri, filename)
            val photoMetadata = extractPhotoMetadata(processedFile)
            val blurScore = calculateBlurScore(processedFile)
            val category = suggestCategory(processedFile, blurScore)
            
            val photo = Photo(
                filename = processedFile.name,
                filePath = processedFile.absolutePath,
                fileSize = processedFile.length(),
                width = photoMetadata.width,
                height = photoMetadata.height,
                category = category,
                blurScore = blurScore,
                createdAt = Date(),
                updatedAt = Date()
            )
            
            Result.success(photo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Create optimized copy of photo in app's private storage
     */
    private suspend fun createProcessedPhoto(
        context: Context,
        sourceUri: Uri,
        customFilename: String?
    ): File = withContext(Dispatchers.IO) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Date())
        val filename = customFilename ?: "PhotoKik_$timestamp.jpg"
        
        val outputFile = File(context.getExternalFilesDir("photos"), filename)
        outputFile.parentFile?.mkdirs()
        
        context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
                ?: throw IOException("Failed to decode image")
            
            val correctedBitmap = correctImageOrientation(context, sourceUri, originalBitmap)
            val optimizedBitmap = optimizeImageSize(correctedBitmap)
            
            FileOutputStream(outputFile).use { outputStream ->
                optimizedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
            }
            
            // Clean up bitmaps
            if (correctedBitmap != originalBitmap) {
                correctedBitmap.recycle()
            }
            if (optimizedBitmap != correctedBitmap) {
                optimizedBitmap.recycle()
            }
            originalBitmap.recycle()
        }
        
        outputFile
    }
    
    /**
     * Extract photo metadata (dimensions, EXIF data)
     */
    private fun extractPhotoMetadata(file: File): PhotoMetadata {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(file.absolutePath, options)
        
        return PhotoMetadata(
            width = options.outWidth,
            height = options.outHeight,
            mimeType = options.outMimeType ?: "image/jpeg"
        )
    }
    
    /**
     * Correct image orientation based on EXIF data
     */
    private fun correctImageOrientation(
        context: Context,
        uri: Uri,
        bitmap: Bitmap
    ): Bitmap {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                
                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                    else -> return bitmap
                }
                
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }
        } catch (e: Exception) {
            // If orientation correction fails, return original bitmap
        }
        
        return bitmap
    }
    
    /**
     * Optimize image size for storage and performance
     */
    private fun optimizeImageSize(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val maxDimension = max(width, height)
        
        if (maxDimension <= MAX_IMAGE_SIZE) {
            return bitmap
        }
        
        val ratio = MAX_IMAGE_SIZE.toFloat() / maxDimension
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    /**
     * Calculate blur score using Laplacian variance method
     */
    private fun calculateBlurScore(file: File): Float {
        return try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return 0f
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
            
            val pixels = IntArray(resizedBitmap.width * resizedBitmap.height)
            resizedBitmap.getPixels(pixels, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)
            
            // Convert to grayscale and apply Laplacian
            val grayscale = pixels.map { pixel ->
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                (0.299 * r + 0.587 * g + 0.114 * b).toInt()
            }
            
            // Calculate Laplacian variance
            var sum = 0.0
            var sumSq = 0.0
            val width = resizedBitmap.width
            val height = resizedBitmap.height
            
            for (y in 1 until height - 1) {
                for (x in 1 until width - 1) {
                    val idx = y * width + x
                    val laplacian = -4 * grayscale[idx] +
                            grayscale[idx - 1] + grayscale[idx + 1] +
                            grayscale[idx - width] + grayscale[idx + width]
                    
                    sum += laplacian
                    sumSq += laplacian * laplacian
                }
            }
            
            val count = (width - 2) * (height - 2)
            val mean = sum / count
            val variance = (sumSq / count) - (mean * mean)
            
            bitmap.recycle()
            if (resizedBitmap != bitmap) {
                resizedBitmap.recycle()
            }
            
            // Normalize to 0-1 range (higher = sharper)
            val normalizedScore = (sqrt(variance) / BLUR_THRESHOLD).coerceIn(0.0, 1.0)
            (1.0 - normalizedScore).toFloat() // Invert so higher = blurrier
            
        } catch (e: Exception) {
            0.5f // Default neutral score if processing fails
        }
    }
    
    /**
     * Suggest category based on image analysis
     */
    private fun suggestCategory(file: File, blurScore: Float): PhotoCategory {
        return when {
            blurScore > 0.7f -> PhotoCategory.BLURRY
            isDocument(file) -> PhotoCategory.DOCUMENTS
            else -> PhotoCategory.UNCATEGORIZED
        }
    }
    
    /**
     * Detect if image is likely a document (screenshot, text-heavy image)
     */
    private fun isDocument(file: File): Boolean {
        // This would use OCR or edge detection to identify documents
        // For now, return false as a placeholder
        return false
    }
    
    /**
     * Create shareable URI for photo
     */
    fun createShareableUri(context: Context, photo: Photo): Uri {
        val file = File(photo.filePath)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
    
    /**
     * Delete photo file from storage
     */
    suspend fun deletePhotoFile(photo: Photo): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(photo.filePath)
            file.delete()
        } catch (e: Exception) {
            false
        }
    }
}

data class PhotoMetadata(
    val width: Int,
    val height: Int,
    val mimeType: String
)
