package com.example.sahajkyc.common
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
fun captureImage(imageCapture: ImageCapture, context: Context, onResult: (Uri?) -> Unit) {
    val outputDirectory = context.filesDir
    val photoFile = File(
        outputDirectory,
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                Log.d("CameraCapture", "Photo capture succeeded: $savedUri")
                onResult(savedUri)
            }
            override fun onError(exc: ImageCaptureException) {
                Log.e("CameraCapture", "Photo capture failed: ${exc.message}", exc)
                onResult(null)
            }
        }
    )
}