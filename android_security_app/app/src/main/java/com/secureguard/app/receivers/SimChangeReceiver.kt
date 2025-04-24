package com.secureguard.app.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.secureguard.app.utils.PreferencesManager
import com.secureguard.app.utils.SecurityUtils

class SimChangeReceiver : BroadcastReceiver() {

    private val TAG = "SimChangeReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_SIM_STATE_CHANGED) {
            Log.i(TAG, "Estado do SIM alterado")
            
            // Verificar se o SIM foi trocado
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val preferencesManager = PreferencesManager(context)
            
            // Obter o IMSI atual
            val currentSimSerial = telephonyManager.simSerialNumber
            val savedSimSerial = preferencesManager.getSavedSimSerial()
            
            if (savedSimSerial.isNotEmpty() && savedSimSerial != currentSimSerial) {
                Log.i(TAG, "Troca de SIM detectada")
                
                // Obter informações do novo SIM
                val operatorName = telephonyManager.networkOperatorName
                val phoneNumber = telephonyManager.line1Number
                
                // Enviar informações por e-mail
                SecurityUtils.sendSimChangeNotification(
                    context,
                    currentSimSerial ?: "Desconhecido",
                    operatorName ?: "Desconhecido",
                    phoneNumber ?: "Desconhecido"
                )
                
                // Salvar novo IMSI
                preferencesManager.saveSimSerial(currentSimSerial ?: "")
            } else if (savedSimSerial.isEmpty() && currentSimSerial != null) {
                // Primeira execução, salvar IMSI atual
                preferencesManager.saveSimSerial(currentSimSerial)
            }
        }
    }
}
