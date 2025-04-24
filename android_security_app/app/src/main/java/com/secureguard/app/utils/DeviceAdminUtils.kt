package com.secureguard.app.utils

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.util.Log

class DeviceAdminUtils(private val context: Context) {
    
    private val TAG = "DeviceAdminUtils"
    private val preferencesManager = PreferencesManager(context)
    private val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val componentName = ComponentName(context, com.secureguard.app.receivers.DeviceAdminReceiver::class.java)
    
    // Verificar se o aplicativo é administrador do dispositivo
    fun isDeviceAdmin(): Boolean {
        return devicePolicyManager.isAdminActive(componentName)
    }
    
    // Bloquear o dispositivo remotamente
    fun lockDevice(): Boolean {
        if (!preferencesManager.isRemoteLockEnabled()) {
            Log.i(TAG, "Bloqueio remoto desativado nas configurações")
            return false
        }
        
        if (!isDeviceAdmin()) {
            Log.e(TAG, "Não é possível bloquear o dispositivo: administrador não está ativo")
            return false
        }
        
        try {
            devicePolicyManager.lockNow()
            Log.i(TAG, "Dispositivo bloqueado remotamente")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao bloquear dispositivo: ${e.message}")
            return false
        }
    }
    
    // Alterar senha do dispositivo remotamente
    fun resetPassword(newPassword: String): Boolean {
        if (!preferencesManager.isRemoteLockEnabled()) {
            Log.i(TAG, "Bloqueio remoto desativado nas configurações")
            return false
        }
        
        if (!isDeviceAdmin()) {
            Log.e(TAG, "Não é possível alterar senha: administrador não está ativo")
            return false
        }
        
        try {
            val result = devicePolicyManager.resetPassword(newPassword, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY)
            Log.i(TAG, "Senha do dispositivo alterada remotamente: $result")
            return result
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao alterar senha: ${e.message}")
            return false
        }
    }
    
    // Limpar dados do dispositivo (factory reset)
    fun wipeData(): Boolean {
        if (!isDeviceAdmin()) {
            Log.e(TAG, "Não é possível limpar dados: administrador não está ativo")
            return false
        }
        
        try {
            // Flags: 0 = sem limpar armazenamento externo
            devicePolicyManager.wipeData(0)
            Log.i(TAG, "Comando de limpeza de dados enviado")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao limpar dados: ${e.message}")
            return false
        }
    }
    
    // Verificar política de senha
    fun isPasswordSufficient(): Boolean {
        if (!isDeviceAdmin()) {
            return false
        }
        
        return devicePolicyManager.isActivePasswordSufficient()
    }
    
    // Obter número de tentativas de senha incorretas
    fun getFailedPasswordAttempts(): Int {
        if (!isDeviceAdmin()) {
            return 0
        }
        
        return devicePolicyManager.currentFailedPasswordAttempts
    }
    
    // Definir tempo máximo de inatividade antes do bloqueio (em milissegundos)
    fun setMaximumTimeToLock(timeMs: Long): Boolean {
        if (!isDeviceAdmin()) {
            return false
        }
        
        try {
            devicePolicyManager.setMaximumTimeToLock(componentName, timeMs)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao definir tempo máximo de inatividade: ${e.message}")
            return false
        }
    }
}
