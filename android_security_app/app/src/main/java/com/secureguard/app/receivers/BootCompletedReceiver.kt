package com.secureguard.app.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.secureguard.app.services.LocationTrackingService
import com.secureguard.app.services.SecurityMonitorService

class BootCompletedReceiver : BroadcastReceiver() {

    private val TAG = "BootCompletedReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i(TAG, "Dispositivo inicializado, iniciando serviços")
            
            // Iniciar serviço de rastreamento de localização
            val locationIntent = Intent(context, LocationTrackingService::class.java)
            context.startService(locationIntent)
            
            // Iniciar serviço de monitoramento de segurança
            val securityIntent = Intent(context, SecurityMonitorService::class.java)
            context.startService(securityIntent)
        }
    }
}
