package com.secureguard.app.utils

import android.content.Context
import android.hardware.camera2.CameraManager
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

class CameraUtils(private val context: Context) {
    
    private val TAG = "CameraUtils"
    private val preferencesManager = PreferencesManager(context)
    
    // Capturar foto da câmera frontal
    fun capturePhoto(onPhotoTaken: (File) -> Unit) {
        if (!preferencesManager.isWrongPasswordPhotoEnabled()) {
            Log.i(TAG, "Captura de foto desativada nas configurações")
            return
        }
        
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                
                // Selecionar câmera frontal
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build()
                
                // Configurar captura de imagem
                val imageCapture = ImageCapture.Builder()
                    .setTargetRotation(android.view.Surface.ROTATION_0)
                    .build()
                
                // Vincular câmera ao ciclo de vida
                cameraProvider.bindToLifecycle(
                    context as LifecycleOwner,
                    cameraSelector,
                    imageCapture
                )
                
                // Criar arquivo para salvar a foto
                val photoFile = createPhotoFile()
                
                // Configurar opções de saída
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                
                // Capturar foto
                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            Log.i(TAG, "Foto capturada com sucesso: ${photoFile.absolutePath}")
                            onPhotoTaken(photoFile)
                        }
                        
                        override fun onError(exception: ImageCaptureException) {
                            Log.e(TAG, "Erro ao capturar foto: ${exception.message}")
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao configurar câmera: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }
    
    // Verificar se o dispositivo tem câmera frontal
    fun hasFrontCamera(): Boolean {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val facing = characteristics.get(android.hardware.camera2.CameraCharacteristics.LENS_FACING)
                if (facing == android.hardware.camera2.CameraCharacteristics.LENS_FACING_FRONT) {
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao verificar câmera frontal: ${e.message}")
        }
        return false
    }
    
    // Criar arquivo para salvar a foto
    private fun createPhotoFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PHOTO_${timeStamp}_", ".jpg", storageDir)
    }
}
