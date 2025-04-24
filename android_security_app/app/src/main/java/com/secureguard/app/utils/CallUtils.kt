package com.secureguard.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log

class CallUtils(private val context: Context) {
    
    private val TAG = "CallUtils"
    private val preferencesManager = PreferencesManager(context)
    
    // Fazer chamada telefônica
    fun makePhoneCall(phoneNumber: String): Boolean {
        if (!preferencesManager.isCallControlEnabled()) {
            Log.i(TAG, "Controle de ligações desativado nas configurações")
            return false
        }
        
        try {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phoneNumber")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            Log.i(TAG, "Chamada iniciada para $phoneNumber")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao iniciar chamada: ${e.message}")
            return false
        }
    }
    
    // Verificar se o dispositivo está em chamada
    fun isInCall(): Boolean {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return when (telephonyManager.callState) {
            TelephonyManager.CALL_STATE_RINGING, 
            TelephonyManager.CALL_STATE_OFFHOOK -> true
            else -> false
        }
    }
    
    // Encerrar chamada atual
    fun endCurrentCall(): Boolean {
        if (!preferencesManager.isCallControlEnabled()) {
            Log.i(TAG, "Controle de ligações desativado nas configurações")
            return false
        }
        
        try {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            telecomManager.endCall()
            Log.i(TAG, "Chamada encerrada")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao encerrar chamada: ${e.message}")
            return false
        }
    }
    
    // Verificar se o dispositivo tem capacidade de fazer chamadas
    fun canMakeCalls(): Boolean {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.phoneType != TelephonyManager.PHONE_TYPE_NONE
    }
    
    // Verificar permissão para fazer chamadas
    fun hasCallPermission(): Boolean {
        return context.checkSelfPermission(android.Manifest.permission.CALL_PHONE) == 
                android.content.pm.PackageManager.PERMISSION_GRANTED
    }
    
    // Formatar número de telefone
    fun formatPhoneNumber(phoneNumber: String): String {
        // Implementação simples, poderia ser mais sofisticada com bibliotecas como libphonenumber
        return phoneNumber.replace(Regex("[^0-9+]"), "")
    }
}
