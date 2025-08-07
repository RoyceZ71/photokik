package com.mocha.photokik.data.database

import androidx.room.*
import com.mocha.photokik.data.model.Photo
import com.mocha.photokik.data.model.PhotoCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    
    @Query("SELECT * FROM photos WHERE is_deleted = 0 ORDER BY created_at DESC")
    fun getAllActivePhotos(): Flow<List<Photo>>
    
    @Query("SELECT * FROM photos WHERE is_deleted = 1 ORDER BY updated_at DESC")
    fun getDeletedPhotos(): Flow<List<Photo>>
    
    @Query("SELECT * FROM photos WHERE id = :id")
    suspend fun getPhotoById(id: Long): Photo?
    
    @Query("SELECT * FROM photos WHERE category = :category AND is_deleted = 0 ORDER BY created_at DESC")
    fun getPhotosByCategory(category: PhotoCategory): Flow<List<Photo>>
    
    @Query("SELECT * FROM photos WHERE is_favorite = 1 AND is_deleted = 0 ORDER BY created_at DESC")
    fun getFavoritePhotos(): Flow<List<Photo>>
    
    @Query("SELECT * FROM photos WHERE blur_score > :threshold AND is_deleted = 0 ORDER BY blur_score DESC")
    fun getBlurryPhotos(threshold: Float = 0.7f): Flow<List<Photo>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: Photo): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<Photo>)
    
    @Update
    suspend fun updatePhoto(photo: Photo)
    
    @Delete
    suspend fun deletePhoto(photo: Photo)
    
    @Query("UPDATE photos SET is_deleted = 1, updated_at = datetime('now') WHERE id = :photoId")
    suspend fun moveToTrash(photoId: Long)
    
    @Query("UPDATE photos SET is_deleted = 0, updated_at = datetime('now') WHERE id = :photoId")
    suspend fun restoreFromTrash(photoId: Long)
    
    @Query("DELETE FROM photos WHERE is_deleted = 1")
    suspend fun emptyTrash()
    
    @Query("UPDATE photos SET is_favorite = :isFavorite, updated_at = datetime('now') WHERE id = :photoId")
    suspend fun updateFavoriteStatus(photoId: Long, isFavorite: Boolean)
    
    @Query("UPDATE photos SET category = :category, updated_at = datetime('now') WHERE id = :photoId")
    suspend fun updatePhotoCategory(photoId: Long, category: PhotoCategory)
    
    // Statistics queries
    @Query("SELECT COUNT(*) FROM photos WHERE is_deleted = 0")
    suspend fun getTotalPhotosCount(): Int
    
    @Query("SELECT COUNT(*) FROM photos WHERE category = :category AND is_deleted = 0")
    suspend fun getPhotosCountByCategory(category: PhotoCategory): Int
    
    @Query("SELECT COUNT(*) FROM photos WHERE is_favorite = 1 AND is_deleted = 0")
    suspend fun getFavoritePhotosCount(): Int
    
    @Query("SELECT SUM(file_size) FROM photos WHERE is_deleted = 0")
    suspend fun getTotalStorageUsed(): Long?
    
    // Search functionality
    @Query("""
        SELECT * FROM photos 
        WHERE (filename LIKE '%' || :query || '%' 
               OR ai_tags LIKE '%' || :query || '%')
        AND is_deleted = 0 
        ORDER BY created_at DESC
    """)
    fun searchPhotos(query: String): Flow<List<Photo>>
}
