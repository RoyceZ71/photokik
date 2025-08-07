package com.mocha.photokik.repository

import com.mocha.photokik.data.database.PhotoDao
import com.mocha.photokik.data.model.Photo
import com.mocha.photokik.data.model.PhotoCategory
import com.mocha.photokik.data.model.GalleryStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(
    private val photoDao: PhotoDao
) {
    
    fun getAllActivePhotos(): Flow<List<Photo>> = photoDao.getAllActivePhotos()
    
    fun getDeletedPhotos(): Flow<List<Photo>> = photoDao.getDeletedPhotos()
    
    fun getPhotosByCategory(category: PhotoCategory): Flow<List<Photo>> = 
        photoDao.getPhotosByCategory(category)
    
    fun getFavoritePhotos(): Flow<List<Photo>> = photoDao.getFavoritePhotos()
    
    fun getBlurryPhotos(threshold: Float = 0.7f): Flow<List<Photo>> = 
        photoDao.getBlurryPhotos(threshold)
    
    suspend fun getPhotoById(id: Long): Photo? = photoDao.getPhotoById(id)
    
    suspend fun insertPhoto(photo: Photo): Long = photoDao.insertPhoto(photo)
    
    suspend fun insertPhotos(photos: List<Photo>) = photoDao.insertPhotos(photos)
    
    suspend fun updatePhoto(photo: Photo) = photoDao.updatePhoto(photo)
    
    suspend fun deletePhoto(photo: Photo) = photoDao.deletePhoto(photo)
    
    suspend fun moveToTrash(photoId: Long) = photoDao.moveToTrash(photoId)
    
    suspend fun restoreFromTrash(photoId: Long) = photoDao.restoreFromTrash(photoId)
    
    suspend fun emptyTrash() = photoDao.emptyTrash()
    
    suspend fun updateFavoriteStatus(photoId: Long, isFavorite: Boolean) = 
        photoDao.updateFavoriteStatus(photoId, isFavorite)
    
    suspend fun updatePhotoCategory(photoId: Long, category: PhotoCategory) = 
        photoDao.updatePhotoCategory(photoId, category)
    
    fun searchPhotos(query: String): Flow<List<Photo>> = photoDao.searchPhotos(query)
    
    // Gallery statistics
    fun getGalleryStats(): Flow<GalleryStats> {
        return combine(
            getAllActivePhotos(),
            getPhotosByCategory(PhotoCategory.MEMORIES),
            getPhotosByCategory(PhotoCategory.DOCUMENTS),
            getPhotosByCategory(PhotoCategory.DUPLICATES),
            getPhotosByCategory(PhotoCategory.BLURRY),
            getFavoritePhotos()
        ) { allPhotos, memories, documents, duplicates, blurry, favorites ->
            
            val storageUsed = allPhotos.sumOf { it.fileSize ?: 0L }
            
            GalleryStats(
                totalPhotos = allPhotos.size,
                memories = memories.size,
                documents = documents.size,
                duplicates = duplicates.size,
                blurry = blurry.size,
                favorites = favorites.size,
                storageUsed = storageUsed
            )
        }
    }
    
    suspend fun processBulkActions(photoIds: List<Long>, action: BulkAction) {
        when (action) {
            BulkAction.MOVE_TO_TRASH -> {
                photoIds.forEach { photoId ->
                    moveToTrash(photoId)
                }
            }
            BulkAction.RESTORE_FROM_TRASH -> {
                photoIds.forEach { photoId ->
                    restoreFromTrash(photoId)
                }
            }
            BulkAction.DELETE_PERMANENTLY -> {
                photoIds.forEach { photoId ->
                    getPhotoById(photoId)?.let { photo ->
                        deletePhoto(photo)
                    }
                }
            }
            BulkAction.MARK_AS_FAVORITE -> {
                photoIds.forEach { photoId ->
                    updateFavoriteStatus(photoId, true)
                }
            }
            BulkAction.UNMARK_AS_FAVORITE -> {
                photoIds.forEach { photoId ->
                    updateFavoriteStatus(photoId, false)
                }
            }
        }
    }
}

enum class BulkAction {
    MOVE_TO_TRASH,
    RESTORE_FROM_TRASH,
    DELETE_PERMANENTLY,
    MARK_AS_FAVORITE,
    UNMARK_AS_FAVORITE
}
