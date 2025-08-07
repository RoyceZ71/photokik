package com.mocha.photokik.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManager @Inject constructor() {
    
    companion object {
        val CAMERA_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )
        
        val STORAGE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
        
        val ALL_PERMISSIONS = CAMERA_PERMISSIONS + STORAGE_PERMISSIONS
    }
    
    /**
     * Check if all required permissions are granted
     */
    fun hasAllPermissions(context: Context): Boolean {
        return ALL_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Check if camera permission is granted
     */
    fun hasCameraPermission(context: Context): Boolean {
        return CAMERA_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Check if storage permissions are granted
     */
    fun hasStoragePermissions(context: Context): Boolean {
        return STORAGE_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Get missing permissions
     */
    fun getMissingPermissions(context: Context): Array<String> {
        return ALL_PERMISSIONS.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    }
    
    /**
     * Get missing camera permissions
     */
    fun getMissingCameraPermissions(context: Context): Array<String> {
        return CAMERA_PERMISSIONS.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    }
    
    /**
     * Get missing storage permissions
     */
    fun getMissingStoragePermissions(context: Context): Array<String> {
        return STORAGE_PERMISSIONS.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    }
    
    /**
     * Check if permission is permanently denied
     */
    fun isPermissionPermanentlyDenied(context: Context, permission: String): Boolean {
        // This would require activity context to check shouldShowRequestPermissionRationale
        // For now, return false as we can't check from service context
        return false
    }
    
    /**
     * Get user-friendly permission names
     */
    fun getPermissionDisplayName(permission: String): String {
        return when (permission) {
            Manifest.permission.CAMERA -> "Camera"
            Manifest.permission.READ_EXTERNAL_STORAGE -> "Storage"
            Manifest.permission.READ_MEDIA_IMAGES -> "Photos"
            Manifest.permission.READ_MEDIA_VIDEO -> "Videos"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "Storage Write"
            else -> permission.substringAfterLast(".")
        }
    }
    
    /**
     * Get permission explanation for user
     */
    fun getPermissionExplanation(permission: String): String {
        return when (permission) {
            Manifest.permission.CAMERA -> "PhotoKik needs camera access to take photos"
            Manifest.permission.READ_EXTERNAL_STORAGE -> "PhotoKik needs storage access to organize your photos"
            Manifest.permission.READ_MEDIA_IMAGES -> "PhotoKik needs access to your photos to organize them"
            Manifest.permission.READ_MEDIA_VIDEO -> "PhotoKik needs access to your videos to organize them"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "PhotoKik needs storage write access to save processed photos"
            else -> "PhotoKik needs this permission to function properly"
        }
    }
}
