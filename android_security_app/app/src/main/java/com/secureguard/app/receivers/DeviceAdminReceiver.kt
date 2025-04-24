package com.secureguard.app.receivers

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.secureguard.app.services.SecurityMonitorService
import com.secureguard.app.utils.SecurityUtils

class DeviceAdminReceiver : DeviceAdminReceiver() {

    private val TAG = "DeviceAdminReceiver"

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.i(TAG, "Administrador de dispositivo habilitado")
        
        // Iniciar serviço de monitoramento de segurança
        val serviceIntent = Intent(context, SecurityMonitorService::class.java)
        context.startService(serviceIntent)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.i(TAG, "Administrador de dispositivo desabilitado")
    }

    override fun onPasswordFailed(context: Context, intent: Intent) {
        super.onPasswordFailed(context, intent)
        Log.i(TAG, "Tentativa de senha incorreta detectada")
        
        // Capturar foto quando a senha for digitada incorretamente
        SecurityUtils.capturePhotoOnWrongPassword(context)
    }

    override fun onPasswordSucceeded(context: Context, intent: Intent) {
        super.onPasswordSucceeded(context, intent)
        Log.i(TAG, "Senha digitada corretamente")
    }

    override fun onLockTaskModeEntering(context: Context, intent: Intent, pkg: String) {
        super.onLockTaskModeEntering(context, intent, pkg)
        Log.i(TAG, "Entrando no modo de bloqueio de tarefa")
    }

    override fun onLockTaskModeExiting(context: Context, intent: Intent) {
        super.onLockTaskModeExiting(context, intent)
        Log.i(TAG, "Saindo do modo de bloqueio de tarefa")
    }
}
