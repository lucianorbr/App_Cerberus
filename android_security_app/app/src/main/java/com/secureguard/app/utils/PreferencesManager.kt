package com.secureguard.app.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

class PreferencesManager(context: Context) {

    private val PREFS_NAME = "com.secureguard.app.preferences"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_SIM_SERIAL = "sim_serial"
        private const val KEY_NOTIFICATION_EMAIL = "notification_email"
        private const val KEY_WIPE_CONFIRMATION_CODE = "wipe_confirmation_code"
        
        // Configurações de funcionalidades
        private const val KEY_LOCATION_TRACKING_ENABLED = "location_tracking_enabled"
        private const val KEY_WRONG_PASSWORD_PHOTO_ENABLED = "wrong_password_photo_enabled"
        private const val KEY_SIM_CHANGE_DETECTION_ENABLED = "sim_change_detection_enabled"
        private const val KEY_REMOTE_LOCK_ENABLED = "remote_lock_enabled"
        private const val KEY_SOUND_ALERT_ENABLED = "sound_alert_enabled"
        private const val KEY_CALL_CONTROL_ENABLED = "call_control_enabled"
    }

    init {
        // Gerar ID de dispositivo único se não existir
        if (getDeviceId().isEmpty()) {
            saveDeviceId(UUID.randomUUID().toString())
        }
        
        // Gerar código de confirmação para limpeza de dados se não existir
        if (getWipeConfirmationCode().isEmpty()) {
            saveWipeConfirmationCode(UUID.randomUUID().toString().substring(0, 8))
        }
    }

    // Métodos para ID de dispositivo
    fun getDeviceId(): String {
        return prefs.getString(KEY_DEVICE_ID, "") ?: ""
    }

    fun saveDeviceId(deviceId: String) {
        prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
    }

    // Métodos para token FCM
    fun getFcmToken(): String {
        return prefs.getString(KEY_FCM_TOKEN, "") ?: ""
    }

    fun saveFcmToken(token: String) {
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
    }

    // Métodos para serial do SIM
    fun getSavedSimSerial(): String {
        return prefs.getString(KEY_SIM_SERIAL, "") ?: ""
    }

    fun saveSimSerial(simSerial: String) {
        prefs.edit().putString(KEY_SIM_SERIAL, simSerial).apply()
    }

    // Métodos para e-mail de notificação
    fun getNotificationEmail(): String {
        return prefs.getString(KEY_NOTIFICATION_EMAIL, "") ?: ""
    }

    fun saveNotificationEmail(email: String) {
        prefs.edit().putString(KEY_NOTIFICATION_EMAIL, email).apply()
    }

    // Métodos para código de confirmação de limpeza
    fun getWipeConfirmationCode(): String {
        return prefs.getString(KEY_WIPE_CONFIRMATION_CODE, "") ?: ""
    }

    fun saveWipeConfirmationCode(code: String) {
        prefs.edit().putString(KEY_WIPE_CONFIRMATION_CODE, code).apply()
    }

    // Métodos para configurações de funcionalidades
    fun isLocationTrackingEnabled(): Boolean {
        return prefs.getBoolean(KEY_LOCATION_TRACKING_ENABLED, false)
    }

    fun setLocationTrackingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LOCATION_TRACKING_ENABLED, enabled).apply()
    }

    fun isWrongPasswordPhotoEnabled(): Boolean {
        return prefs.getBoolean(KEY_WRONG_PASSWORD_PHOTO_ENABLED, false)
    }

    fun setWrongPasswordPhotoEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_WRONG_PASSWORD_PHOTO_ENABLED, enabled).apply()
    }

    fun isSimChangeDetectionEnabled(): Boolean {
        return prefs.getBoolean(KEY_SIM_CHANGE_DETECTION_ENABLED, false)
    }

    fun setSimChangeDetectionEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SIM_CHANGE_DETECTION_ENABLED, enabled).apply()
    }

    fun isRemoteLockEnabled(): Boolean {
        return prefs.getBoolean(KEY_REMOTE_LOCK_ENABLED, false)
    }

    fun setRemoteLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REMOTE_LOCK_ENABLED, enabled).apply()
    }

    fun isSoundAlertEnabled(): Boolean {
        return prefs.getBoolean(KEY_SOUND_ALERT_ENABLED, false)
    }

    fun setSoundAlertEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ALERT_ENABLED, enabled).apply()
    }

    fun isCallControlEnabled(): Boolean {
        return prefs.getBoolean(KEY_CALL_CONTROL_ENABLED, false)
    }

    fun setCallControlEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_CALL_CONTROL_ENABLED, enabled).apply()
    }
}
