package com.mocha.photokik

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class PhotoKikApplication : Application() {
    
    companion object {
        const val PHOTO_PROCESSING_CHANNEL_ID = "photo_processing_channel"
    }
    
    override fun onCreate() {
        super.onCreate()
        setupNotificationChannels()
    }
    
    private fun setupNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            
            val channel = NotificationChannel(
                PHOTO_PROCESSING_CHANNEL_ID,
                "Photo Processing",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for photo processing tasks"
                setShowBadge(false)
                enableVibration(false)
            }
            
            notificationManager.createNotificationChannel(channel)
        }
    }
}
