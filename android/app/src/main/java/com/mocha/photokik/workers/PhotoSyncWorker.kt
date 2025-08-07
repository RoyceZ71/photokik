package com.mocha.photokik.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.mocha.photokik.repository.PhotoRepository
import com.mocha.photokik.utils.GalleryManager
import com.mocha.photokik.utils.PermissionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class PhotoSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val photoRepository: PhotoRepository,
    private val galleryManager: GalleryManager,
    private val permissionManager: PermissionManager
) : CoroutineWorker(context, workerParams) {
    
    companion object {
        const val WORK_NAME = "photo_sync_work"
        private const val MAX_PHOTOS_PER_SYNC = 100
        
        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .build()
            
            val workRequest = PeriodicWorkRequestBuilder<PhotoSyncWorker>(
                repeatInterval = 6,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
            
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )
        }
        
        fun scheduleOneTimeSync(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<PhotoSyncWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "one_time_sync",
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
        }
    }
    
    override suspend fun doWork(): Result {
        return try {
            // Check permissions
            if (!permissionManager.hasStoragePermissions(applicationContext)) {
                return Result.failure()
            }
            
            // Set progress
            setProgress(workDataOf("status" to "Starting sync..."))
            
            // Get current photos from database
            val existingPhotos = photoRepository.getAllActivePhotos().first()
            val existingPaths = existingPhotos.map { it.filePath }.toSet()
            
            // Import recent photos from gallery
            val recentPhotosResult = galleryManager.importRecentPhotos(applicationContext, 7)
            
            if (recentPhotosResult.isFailure) {
                return Result.retry()
            }
            
            val recentPhotos = recentPhotosResult.getOrThrow()
            val newPhotos = recentPhotos.filter { it.filePath !in existingPaths }
                .take(MAX_PHOTOS_PER_SYNC)
            
            if (newPhotos.isEmpty()) {
                setProgress(workDataOf("status" to "No new photos found"))
                return Result.success()
            }
            
            // Import new photos
            var imported = 0
            for (photo in newPhotos) {
                try {
                    photoRepository.insertPhoto(photo)
                    imported++
                    
                    val progress = (imported * 100) / newPhotos.size
                    setProgress(workDataOf(
                        "status" to "Importing photos...",
                        "progress" to progress,
                        "imported" to imported,
                        "total" to newPhotos.size
                    ))
                    
                } catch (e: Exception) {
                    // Continue with next photo
                }
            }
            
            setProgress(workDataOf(
                "status" to "Sync completed",
                "imported" to imported
            ))
            
            Result.success(workDataOf("imported_count" to imported))
            
        } catch (e: Exception) {
            Result.failure(workDataOf("error" to e.message))
        }
    }
}
