package com.mocha.photokik.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mocha.photokik.PhotoKikApplication
import com.mocha.photokik.R
import com.mocha.photokik.data.model.Photo
import com.mocha.photokik.repository.PhotoRepository
import com.mocha.photokik.utils.AIPhotoAnalyzer
import com.mocha.photokik.utils.DuplicateDetector
import com.mocha.photokik.utils.PhotoProcessor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class PhotoProcessingService : Service() {
    
    @Inject lateinit var photoRepository: PhotoRepository
    @Inject lateinit var photoProcessor: PhotoProcessor
    @Inject lateinit var aiAnalyzer: AIPhotoAnalyzer
    @Inject lateinit var duplicateDetector: DuplicateDetector
    
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    
    companion object {
        const val ACTION_PROCESS_PHOTO = "com.mocha.photokik.PROCESS_PHOTO"
        const val ACTION_ANALYZE_PHOTOS = "com.mocha.photokik.ANALYZE_PHOTOS"
        const val ACTION_DETECT_DUPLICATES = "com.mocha.photokik.DETECT_DUPLICATES"
        const val EXTRA_PHOTO_ID = "photo_id"
        const val NOTIFICATION_ID = 1001
        
        fun startPhotoProcessing(context: Context, photoId: Long) {
            val intent = Intent(context, PhotoProcessingService::class.java).apply {
                action = ACTION_PROCESS_PHOTO
                putExtra(EXTRA_PHOTO_ID, photoId)
            }
            ContextCompat.startForegroundService(context, intent)
        }
        
        fun startPhotoAnalysis(context: Context) {
            val intent = Intent(context, PhotoProcessingService::class.java).apply {
                action = ACTION_ANALYZE_PHOTOS
            }
            ContextCompat.startForegroundService(context, intent)
        }
        
        fun startDuplicateDetection(context: Context) {
            val intent = Intent(context, PhotoProcessingService::class.java).apply {
                action = ACTION_DETECT_DUPLICATES
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PROCESS_PHOTO -> {
                val photoId = intent.getLongExtra(EXTRA_PHOTO_ID, -1L)
                if (photoId != -1L) {
                    startForeground(NOTIFICATION_ID, createProcessingNotification("Processing photo..."))
                    serviceScope.launch {
                        processPhoto(photoId)
                        stopSelf(startId)
                    }
                }
            }
            
            ACTION_ANALYZE_PHOTOS -> {
                startForeground(NOTIFICATION_ID, createProcessingNotification("Analyzing photos..."))
                serviceScope.launch {
                    analyzeAllPhotos()
                    stopSelf(startId)
                }
            }
            
            ACTION_DETECT_DUPLICATES -> {
                startForeground(NOTIFICATION_ID, createProcessingNotification("Detecting duplicates..."))
                serviceScope.launch {
                    detectDuplicates()
                    stopSelf(startId)
                }
            }
        }
        
        return START_NOT_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
    
    private suspend fun processPhoto(photoId: Long) = withContext(Dispatchers.IO) {
        try {
            val photo = photoRepository.getPhotoById(photoId) ?: return@withContext
            
            // Analyze photo with AI
            val newCategory = aiAnalyzer.analyzePhoto(this@PhotoProcessingService, photo)
            val tags = aiAnalyzer.generateTags(this@PhotoProcessingService, photo)
            
            // Update photo with analysis results
            val updatedPhoto = photo.copy(
                category = newCategory,
                aiTags = tags.joinToString(", ")
            )
            
            photoRepository.updatePhoto(updatedPhoto)
            
            // Update notification
            updateNotification("Photo processed successfully")
            
        } catch (e: Exception) {
            updateNotification("Failed to process photo")
        }
    }
    
    private suspend fun analyzeAllPhotos() = withContext(Dispatchers.IO) {
        try {
            photoRepository.getAllActivePhotos().collect { photos ->
                val totalPhotos = photos.size
                var processedCount = 0
                
                for (photo in photos) {
                    try {
                        // Skip if already analyzed
                        if (photo.aiTags?.isNotEmpty() == true) continue
                        
                        val newCategory = aiAnalyzer.analyzePhoto(this@PhotoProcessingService, photo)
                        val tags = aiAnalyzer.generateTags(this@PhotoProcessingService, photo)
                        
                        val updatedPhoto = photo.copy(
                            category = newCategory,
                            aiTags = tags.joinToString(", ")
                        )
                        
                        photoRepository.updatePhoto(updatedPhoto)
                        processedCount++
                        
                        // Update progress
                        val progress = (processedCount * 100) / totalPhotos
                        updateNotification("Analyzing photos... $progress%")
                        
                    } catch (e: Exception) {
                        // Continue with next photo
                    }
                }
                
                updateNotification("Photo analysis completed")
                return@collect // Exit the collect loop
            }
            
        } catch (e: Exception) {
            updateNotification("Failed to analyze photos")
        }
    }
    
    private suspend fun detectDuplicates() = withContext(Dispatchers.IO) {
        try {
            photoRepository.getAllActivePhotos().collect { photos ->
                val duplicateGroups = duplicateDetector.findDuplicates(photos)
                
                // Mark duplicates
                for ((original, duplicates) in duplicateGroups) {
                    for (duplicate in duplicates) {
                        photoRepository.updatePhotoCategory(duplicate.id, com.mocha.photokik.data.model.PhotoCategory.DUPLICATES)
                    }
                }
                
                updateNotification("Found ${duplicateGroups.size} duplicate groups")
                return@collect // Exit the collect loop
            }
            
        } catch (e: Exception) {
            updateNotification("Failed to detect duplicates")
        }
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            PhotoKikApplication.PHOTO_PROCESSING_CHANNEL_ID,
            getString(R.string.photo_processing_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.photo_processing_channel_description)
            setShowBadge(false)
        }
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    
    private fun createProcessingNotification(content: String): Notification {
        return NotificationCompat.Builder(this, PhotoKikApplication.PHOTO_PROCESSING_CHANNEL_ID)
            .setContentTitle("PhotoKik")
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setProgress(0, 0, true)
            .build()
    }
    
    private fun updateNotification(content: String) {
        val notification = NotificationCompat.Builder(this, PhotoKikApplication.PHOTO_PROCESSING_CHANNEL_ID)
            .setContentTitle("PhotoKik")
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(false)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
