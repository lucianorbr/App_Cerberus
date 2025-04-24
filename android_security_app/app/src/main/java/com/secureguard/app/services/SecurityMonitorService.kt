package com.secureguard.app.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.secureguard.app.R
import com.secureguard.app.utils.PreferencesManager
import com.secureguard.app.utils.SecurityUtils

class SecurityMonitorService : Service() {

    private val TAG = "SecurityMonitorService"
    private lateinit var preferencesManager: PreferencesManager

    companion object {
        private const val NOTIFICATION_ID = 54321
        private const val CHANNEL_ID = "security_monitor_channel"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Serviço de monitoramento de segurança criado")
        
        preferencesManager = PreferencesManager(this)
        
        // Registrar para tópicos do Firebase Cloud Messaging
        registerForFcmTopics()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Serviço de monitoramento de segurança iniciado")
        
        // Criar canal de notificação para Android 8.0+
        createNotificationChannel()
        
        // Iniciar como serviço em primeiro plano
        startForeground(NOTIFICATION_ID, createNotification())
        
        // Verificar configurações de segurança
        checkSecuritySettings()
        
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Serviço de monitoramento de segurança destruído")
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Monitoramento de Segurança"
            val descriptionText = "Canal para notificações de monitoramento de segurança"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SecureGuard")
            .setContentText("Monitoramento de segurança ativo")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun registerForFcmTopics() {
        val deviceId = preferencesManager.getDeviceId()
        
        // Registrar para tópico específico do dispositivo
        FirebaseMessaging.getInstance().subscribeToTopic("device_$deviceId")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "Registrado para tópico device_$deviceId")
                } else {
                    Log.e(TAG, "Falha ao registrar para tópico: ${task.exception}")
                }
            }
        
        // Registrar para tópico de comandos
        FirebaseMessaging.getInstance().subscribeToTopic("commands")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "Registrado para tópico commands")
                } else {
                    Log.e(TAG, "Falha ao registrar para tópico: ${task.exception}")
                }
            }
    }
    
    private fun checkSecuritySettings() {
        // Verificar se as configurações de segurança estão ativas
        if (preferencesManager.isSimChangeDetectionEnabled()) {
            SecurityUtils.registerSimChangeDetection(this)
        }
        
        if (preferencesManager.isWrongPasswordPhotoEnabled()) {
            SecurityUtils.registerWrongPasswordDetection(this)
        }
        
        if (preferencesManager.isRemoteLockEnabled()) {
            SecurityUtils.registerRemoteLock(this)
        }
        
        if (preferencesManager.isSoundAlertEnabled()) {
            SecurityUtils.registerSoundAlert(this)
        }
        
        if (preferencesManager.isCallControlEnabled()) {
            SecurityUtils.registerCallControl(this)
        }
    }
}
