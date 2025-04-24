package com.secureguard.app.utils

import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log

class SimUtils(private val context: Context) {
    
    private val TAG = "SimUtils"
    private val preferencesManager = PreferencesManager(context)
    
    // Obter informações do SIM atual
    fun getCurrentSimInfo(): SimInfo {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        
        val simSerial = try {
            telephonyManager.simSerialNumber ?: ""
        } catch (e: SecurityException) {
            Log.e(TAG, "Erro ao obter número de série do SIM: ${e.message}")
            ""
        }
        
        val operatorName = telephonyManager.networkOperatorName ?: ""
        
        val phoneNumber = try {
            telephonyManager.line1Number ?: ""
        } catch (e: SecurityException) {
            Log.e(TAG, "Erro ao obter número de telefone: ${e.message}")
            ""
        }
        
        return SimInfo(simSerial, operatorName, phoneNumber)
    }
    
    // Verificar se o SIM foi trocado
    fun checkSimChanged(): Boolean {
        if (!preferencesManager.isSimChangeDetectionEnabled()) {
            return false
        }
        
        val currentSimInfo = getCurrentSimInfo()
        val savedSimSerial = preferencesManager.getSavedSimSerial()
        
        // Se não há SIM salvo, salvar o atual e retornar falso
        if (savedSimSerial.isEmpty()) {
            preferencesManager.saveSimSerial(currentSimInfo.simSerial)
            return false
        }
        
        // Verificar se o SIM atual é diferente do salvo
        val simChanged = savedSimSerial != currentSimInfo.simSerial && currentSimInfo.simSerial.isNotEmpty()
        
        if (simChanged) {
            Log.i(TAG, "Troca de SIM detectada. Anterior: $savedSimSerial, Atual: ${currentSimInfo.simSerial}")
        }
        
        return simChanged
    }
    
    // Salvar informações do SIM atual
    fun saveCurrentSimInfo() {
        val currentSimInfo = getCurrentSimInfo()
        preferencesManager.saveSimSerial(currentSimInfo.simSerial)
        Log.i(TAG, "Informações do SIM salvas: ${currentSimInfo.simSerial}")
    }
    
    // Verificar se o dispositivo tem um SIM inserido
    fun hasSimCard(): Boolean {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.simState == TelephonyManager.SIM_STATE_READY
    }
    
    // Classe de dados para informações do SIM
    data class SimInfo(
        val simSerial: String,
        val operatorName: String,
        val phoneNumber: String
    )
}
