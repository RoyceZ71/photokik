package com.mocha.photokik.utils

import android.content.Context
import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.mocha.photokik.data.model.Photo
import com.mocha.photokik.data.model.PhotoCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraManager @Inject constructor() {
    
    private var imageCapture: ImageCapture? = null
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    
    fun startCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onCameraStarted: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                
                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                
                // ImageCapture
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetRotation(previewView.display.rotation)
                    .build()
                
                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, 
                    cameraSelector, 
                    preview, 
                    imageCapture
                )
                
                onCameraStarted()
                
            } catch (exc: Exception) {
                onError(exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }
    
    suspend fun capturePhoto(
        context: Context,
        onPhotoSaved: (Photo) -> Unit,
        onError: (ImageCaptureException) -> Unit
    ) = withContext(Dispatchers.IO) {
        
        val imageCapture = imageCapture ?: return@withContext
        
        // Create time stamped name and MediaStore entry
        val name = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
            .format(System.currentTimeMillis())
        
        // Create output file in the app's private directory
        val photoFile = File(
            context.getExternalFilesDir(null),
            "PhotoKik_$name.jpg"
        )
        
        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        
        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
                
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    
                    // Create Photo object
                    val photo = Photo(
                        filename = "PhotoKik_$name.jpg",
                        filePath = photoFile.absolutePath,
                        fileSize = photoFile.length(),
                        category = PhotoCategory.UNCATEGORIZED,
                        createdAt = Date(),
                        updatedAt = Date()
                    )
                    
                    onPhotoSaved(photo)
                }
            }
        )
    }
    
    fun hasFlash(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(
            android.content.pm.PackageManager.FEATURE_CAMERA_FLASH
        )
    }
    
    fun toggleFlash(enabled: Boolean) {
        imageCapture?.flashMode = if (enabled) {
            ImageCapture.FLASH_MODE_ON
        } else {
            ImageCapture.FLASH_MODE_OFF
        }
    }
    
    fun cleanup() {
        cameraExecutor.shutdown()
    }
}
