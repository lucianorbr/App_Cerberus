package com.secureguard.app.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.secureguard.app.utils.PreferencesManager
import com.secureguard.app.utils.SecurityUtils

class FirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FirebaseMessagingService"
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferencesManager(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.i(TAG, "Mensagem recebida do Firebase: ${remoteMessage.data}")
        
        // Processar comandos remotos
        processRemoteCommand(remoteMessage.data)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "Novo token FCM recebido: $token")
        
        // Salvar o token para uso posterior
        preferencesManager.saveFcmToken(token)
        
        // Enviar o token para o servidor
        val deviceId = preferencesManager.getDeviceId()
        if (deviceId.isNotEmpty()) {
            // Implementação para enviar o token para o servidor
            // Será implementado posteriormente
        }
    }
    
    private fun processRemoteCommand(data: Map<String, String>) {
        when (data["command"]) {
            "lock_device" -> {
                if (preferencesManager.isRemoteLockEnabled()) {
                    SecurityUtils.lockDevice(this)
                }
            }
            "sound_alert" -> {
                if (preferencesManager.isSoundAlertEnabled()) {
                    SecurityUtils.playSoundAlert(this)
                }
            }
            "make_call" -> {
                if (preferencesManager.isCallControlEnabled()) {
                    val phoneNumber = data["phone_number"]
                    if (!phoneNumber.isNullOrEmpty()) {
                        SecurityUtils.makePhoneCall(this, phoneNumber)
                    }
                }
            }
            "wipe_data" -> {
                // Esta é uma operação perigosa, então verificamos uma confirmação adicional
                val confirmationCode = data["confirmation_code"]
                val savedConfirmationCode = preferencesManager.getWipeConfirmationCode()
                
                if (!confirmationCode.isNullOrEmpty() && confirmationCode == savedConfirmationCode) {
                    SecurityUtils.wipeDeviceData(this)
                }
            }
            "update_settings" -> {
                // Atualizar configurações remotamente
                data["location_tracking"]?.let {
                    preferencesManager.setLocationTrackingEnabled(it.toBoolean())
                }
                data["wrong_password_photo"]?.let {
                    preferencesManager.setWrongPasswordPhotoEnabled(it.toBoolean())
                }
                data["sim_change_detection"]?.let {
                    preferencesManager.setSimChangeDetectionEnabled(it.toBoolean())
                }
                data["remote_lock"]?.let {
                    preferencesManager.setRemoteLockEnabled(it.toBoolean())
                }
                data["sound_alert"]?.let {
                    preferencesManager.setSoundAlertEnabled(it.toBoolean())
                }
                data["call_control"]?.let {
                    preferencesManager.setCallControlEnabled(it.toBoolean())
                }
                
                // Reiniciar serviço de monitoramento para aplicar novas configurações
                val intent = Intent(this, SecurityMonitorService::class.java)
                startService(intent)
            }
        }
    }
}
