package com.mocha.photokik.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.mocha.photokik.data.model.Photo
import com.mocha.photokik.data.model.PhotoCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryManager @Inject constructor(
    private val photoProcessor: PhotoProcessor
) {
    companion object {
        private const val TAG = "GalleryManager"
        
        private val EXTERNAL_CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        private val PROJECTION = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.MIME_TYPE
        )
    }
    
    /**
     * Import all photos from device gallery
     */
    suspend fun importAllPhotos(context: Context): Result<List<Photo>> = withContext(Dispatchers.IO) {
        try {
            val photos = mutableListOf<Photo>()
            val contentResolver = context.contentResolver
            
            val cursor = contentResolver.query(
                EXTERNAL_CONTENT_URI,
                PROJECTION,
                null,
                null,
                "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
            )
            
            cursor?.use {
                while (it.moveToNext()) {
                    try {
                        val photo = createPhotoFromCursor(it)
                        photos.add(photo)
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to process photo from cursor", e)
                    }
                }
            }
            
            Result.success(photos)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to import photos from gallery", e)
            Result.failure(e)
        }
    }
    
    /**
     * Import photos from gallery as Flow for reactive updates
     */
    fun importPhotosAsFlow(context: Context): Flow<List<Photo>> = flow {
        val contentResolver = context.contentResolver
        val photos = mutableListOf<Photo>()
        
        val cursor = contentResolver.query(
            EXTERNAL_CONTENT_URI,
            PROJECTION,
            null,
            null,
            "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                try {
                    val photo = createPhotoFromCursor(it)
                    photos.add(photo)
                    
                    // Emit partial results for progressive loading
                    if (photos.size % 50 == 0) {
                        emit(photos.toList())
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to process photo from cursor", e)
                }
            }
        }
        
        emit(photos)
    }.flowOn(Dispatchers.IO)
    
    /**
     * Import recent photos (last 30 days)
     */
    suspend fun importRecentPhotos(context: Context, daysSince: Int = 30): Result<List<Photo>> = 
        withContext(Dispatchers.IO) {
        try {
            val photos = mutableListOf<Photo>()
            val contentResolver = context.contentResolver
            val thirtyDaysAgo = (System.currentTimeMillis() / 1000) - (daysSince * 24 * 60 * 60)
            
            val selection = "${MediaStore.Images.Media.DATE_ADDED} >= ?"
            val selectionArgs = arrayOf(thirtyDaysAgo.toString())
            
            val cursor = contentResolver.query(
                EXTERNAL_CONTENT_URI,
                PROJECTION,
                selection,
                selectionArgs,
                "${MediaStore.Images.Media.DATE_ADDED} DESC"
            )
            
            cursor?.use {
                while (it.moveToNext()) {
                    try {
                        val photo = createPhotoFromCursor(it)
                        photos.add(photo)
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to process photo from cursor", e)
                    }
                }
            }
            
            Result.success(photos)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to import recent photos", e)
            Result.failure(e)
        }
    }
    
    /**
     * Create Photo object from MediaStore cursor
     */
    private fun createPhotoFromCursor(cursor: Cursor): Photo {
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
        val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
        val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
        val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
        val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
        
        val id = cursor.getLong(idColumn)
        val name = cursor.getString(nameColumn) ?: "Unknown"
        val data = cursor.getString(dataColumn) ?: ""
        val size = cursor.getLong(sizeColumn)
        val width = cursor.getInt(widthColumn)
        val height = cursor.getInt(heightColumn)
        val dateAdded = cursor.getLong(dateAddedColumn) * 1000 // Convert to milliseconds
        val dateModified = cursor.getLong(dateModifiedColumn) * 1000
        
        return Photo(
            filename = name,
            filePath = data,
            fileSize = size,
            width = width,
            height = height,
            category = PhotoCategory.UNCATEGORIZED,
            createdAt = Date(dateAdded),
            updatedAt = Date(dateModified)
        )
    }
    
    /**
     * Get content URI for a photo ID
     */
    fun getPhotoUri(photoId: Long): Uri {
        return ContentUris.withAppendedId(EXTERNAL_CONTENT_URI, photoId)
    }
    
    /**
     * Check if photo exists in MediaStore
     */
    suspend fun photoExists(context: Context, photoPath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(photoPath)
            file.exists() && file.canRead()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get photo count from MediaStore
     */
    suspend fun getPhotoCount(context: Context): Int = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(
                EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID),
                null,
                null,
                null
            )
            cursor?.use { it.count } ?: 0
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Search photos by filename
     */
    suspend fun searchPhotos(context: Context, query: String): Result<List<Photo>> = 
        withContext(Dispatchers.IO) {
        try {
            val photos = mutableListOf<Photo>()
            val contentResolver = context.contentResolver
            
            val selection = "${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?"
            val selectionArgs = arrayOf("%$query%")
            
            val cursor = contentResolver.query(
                EXTERNAL_CONTENT_URI,
                PROJECTION,
                selection,
                selectionArgs,
                "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
            )
            
            cursor?.use {
                while (it.moveToNext()) {
                    try {
                        val photo = createPhotoFromCursor(it)
                        photos.add(photo)
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to process photo from search", e)
                    }
                }
            }
            
            Result.success(photos)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to search photos", e)
            Result.failure(e)
        }
    }
}
