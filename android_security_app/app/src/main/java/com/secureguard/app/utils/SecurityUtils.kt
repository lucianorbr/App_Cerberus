package com.secureguard.app.utils

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.telephony.TelephonyManager
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.secureguard.app.R
import com.secureguard.app.receivers.SimChangeReceiver
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Properties
import java.util.concurrent.Executors
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

object SecurityUtils {

    private val TAG = "SecurityUtils"
    private var mediaPlayer: MediaPlayer? = null

    // Capturar foto quando a senha for digitada incorretamente
    fun capturePhotoOnWrongPassword(context: Context) {
        val preferencesManager = PreferencesManager(context)
        
        if (!preferencesManager.isWrongPasswordPhotoEnabled()) {
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
                val photoFile = createPhotoFile(context)
                
                // Configurar opções de saída
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                
                // Capturar foto
                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            Log.i(TAG, "Foto capturada com sucesso: ${photoFile.absolutePath}")
                            
                            // Enviar foto por e-mail
                            sendEmailWithAttachment(
                                context,
                                "Tentativa de senha incorreta detectada",
                                "Uma tentativa de senha incorreta foi detectada no seu dispositivo. " +
                                "A foto anexada foi capturada no momento da tentativa.",
                                photoFile
                            )
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
    
    // Enviar notificação de troca de SIM
    fun sendSimChangeNotification(context: Context, simSerial: String, operatorName: String, phoneNumber: String) {
        val preferencesManager = PreferencesManager(context)
        
        if (!preferencesManager.isSimChangeDetectionEnabled()) {
            return
        }
        
        val subject = "Troca de SIM detectada"
        val body = """
            Uma troca de SIM foi detectada no seu dispositivo.
            
            Detalhes do novo SIM:
            - Número de série: $simSerial
            - Operadora: $operatorName
            - Número de telefone: $phoneNumber
            
            Se você não realizou esta troca, seu dispositivo pode ter sido comprometido.
        """.trimIndent()
        
        sendEmail(context, subject, body)
    }
    
    // Bloquear dispositivo remotamente
    fun lockDevice(context: Context) {
        val preferencesManager = PreferencesManager(context)
        
        if (!preferencesManager.isRemoteLockEnabled()) {
            return
        }
        
        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(context, com.secureguard.app.receivers.DeviceAdminReceiver::class.java)
        
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.lockNow()
            Log.i(TAG, "Dispositivo bloqueado remotamente")
        } else {
            Log.e(TAG, "Não é possível bloquear o dispositivo: administrador não está ativo")
        }
    }
    
    // Reproduzir alerta sonoro
    fun playSoundAlert(context: Context) {
        val preferencesManager = PreferencesManager(context)
        
        if (!preferencesManager.isSoundAlertEnabled()) {
            return
        }
        
        // Configurar volume máximo
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0)
        
        // Reproduzir som de alerta
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
            Log.i(TAG, "Alerta sonoro iniciado")
        } else if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
            Log.i(TAG, "Alerta sonoro reiniciado")
        }
    }
    
    // Parar alerta sonoro
    fun stopSoundAlert() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
                mediaPlayer = null
                Log.i(TAG, "Alerta sonoro parado")
            }
        }
    }
    
    // Fazer chamada telefônica
    fun makePhoneCall(context: Context, phoneNumber: String) {
        val preferencesManager = PreferencesManager(context)
        
        if (!preferencesManager.isCallControlEnabled()) {
            return
        }
        
        try {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phoneNumber")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            Log.i(TAG, "Chamada iniciada para $phoneNumber")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao iniciar chamada: ${e.message}")
        }
    }
    
    // Limpar dados do dispositivo
    fun wipeDeviceData(context: Context) {
        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(context, com.secureguard.app.receivers.DeviceAdminReceiver::class.java)
        
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.wipeData(0)
            Log.i(TAG, "Comando de limpeza de dados enviado")
        } else {
            Log.e(TAG, "Não é possível limpar dados: administrador não está ativo")
        }
    }
    
    // Registrar detecção de troca de SIM
    fun registerSimChangeDetection(context: Context) {
        val simChangeReceiver = SimChangeReceiver()
        val filter = IntentFilter(TelephonyManager.ACTION_SIM_STATE_CHANGED)
        context.registerReceiver(simChangeReceiver, filter)
        Log.i(TAG, "Receptor de troca de SIM registrado")
    }
    
    // Registrar detecção de senha incorreta
    fun registerWrongPasswordDetection(context: Context) {
        // Esta funcionalidade é implementada através do DeviceAdminReceiver
        Log.i(TAG, "Detecção de senha incorreta configurada")
    }
    
    // Registrar bloqueio remoto
    fun registerRemoteLock(context: Context) {
        // Esta funcionalidade é implementada através do FirebaseMessagingService
        Log.i(TAG, "Bloqueio remoto configurado")
    }
    
    // Registrar alerta sonoro
    fun registerSoundAlert(context: Context) {
        // Esta funcionalidade é implementada através do FirebaseMessagingService
        Log.i(TAG, "Alerta sonoro configurado")
    }
    
    // Registrar controle de ligações
    fun registerCallControl(context: Context) {
        // Esta funcionalidade é implementada através do FirebaseMessagingService
        Log.i(TAG, "Controle de ligações configurado")
    }
    
    // Criar arquivo para salvar foto
    private fun createPhotoFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PHOTO_${timeStamp}_", ".jpg", storageDir)
    }
    
    // Enviar e-mail
    private fun sendEmail(context: Context, subject: String, body: String) {
        val preferencesManager = PreferencesManager(context)
        val email = preferencesManager.getNotificationEmail()
        
        if (email.isEmpty()) {
            Log.e(TAG, "Não é possível enviar e-mail: e-mail de notificação não configurado")
            return
        }
        
        Thread {
            try {
                val props = Properties()
                props["mail.smtp.host"] = "smtp.gmail.com"
                props["mail.smtp.socketFactory.port"] = "465"
                props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
                props["mail.smtp.auth"] = "true"
                props["mail.smtp.port"] = "465"
                
                val session = Session.getDefaultInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication("secureguard.app@gmail.com", "app_password")
                    }
                })
                
                val message = MimeMessage(session)
                message.setFrom(InternetAddress("secureguard.app@gmail.com"))
                message.addRecipient(Message.RecipientType.TO, InternetAddress(email))
                message.subject = subject
                message.setText(body)
                
                Transport.send(message)
                Log.i(TAG, "E-mail enviado com sucesso para $email")
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao enviar e-mail: ${e.message}")
            }
        }.start()
    }
    
    // Enviar e-mail com anexo
    private fun sendEmailWithAttachment(context: Context, subject: String, body: String, attachment: File) {
        val preferencesManager = PreferencesManager(context)
        val email = preferencesManager.getNotificationEmail()
        
        if (email.isEmpty()) {
            Log.e(TAG, "Não é possível enviar e-mail: e-mail de notificação não configurado")
            return
        }
        
        Thread {
            try {
                val props = Properties()
                props["mail.smtp.host"] = "smtp.gmail.com"
                props["mail.smtp.socketFactory.port"] = "465"
                props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
                props["mail.smtp.auth"] = "true"
                props["mail.smtp.port"] = "465"
                
                val session = Session.getDefaultInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication("secureguard.app@gmail.com", "app_password")
                    }
                })
                
                val message = MimeMessage(session)
                message.setFrom(InternetAddress("secureguard.app@gmail.com"))
                message.addRecipient(Message.RecipientType.TO, InternetAddress(email))
                message.subject = subject
                
                // Criar parte de texto
                val textPart = MimeBodyPart()
                textPart.setText(body)
                
                // Criar parte de anexo
                val attachmentPart = MimeBodyPart()
                attachmentPart.attachFile(attachment)
                
                // Criar multipart e adicionar partes
                val multipart = MimeMultipart()
                multipart.addBodyPart(textPart)
                multipart.addBodyPart(attachmentPart)
                
                // Definir conteúdo da mensagem
                message.setContent(multipart)
                
                Transport.send(message)
                Log.i(TAG, "E-mail com anexo enviado com sucesso para $email")
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao enviar e-mail com anexo: ${e.message}")
            }
        }.start()
    }
}
