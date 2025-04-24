package com.secureguard.app.utils

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.secureguard.app.R

class SoundUtils(private val context: Context) {
    
    private val TAG = "SoundUtils"
    private val preferencesManager = PreferencesManager(context)
    private var mediaPlayer: MediaPlayer? = null
    
    // Reproduzir alerta sonoro
    fun playSoundAlert(): Boolean {
        if (!preferencesManager.isSoundAlertEnabled()) {
            Log.i(TAG, "Alerta sonoro desativado nas configurações")
            return false
        }
        
        try {
            // Configurar volume máximo
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0)
            
            // Parar reprodução anterior se existir
            stopSoundAlert()
            
            // Reproduzir som de alerta
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound)
            mediaPlayer?.apply {
                isLooping = true
                start()
            }
            
            Log.i(TAG, "Alerta sonoro iniciado")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao reproduzir alerta sonoro: ${e.message}")
            return false
        }
    }
    
    // Parar alerta sonoro
    fun stopSoundAlert(): Boolean {
        return try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
                mediaPlayer = null
                Log.i(TAG, "Alerta sonoro parado")
                true
            } ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao parar alerta sonoro: ${e.message}")
            false
        }
    }
    
    // Verificar se o alerta sonoro está tocando
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
    
    // Obter volume atual
    fun getCurrentVolume(): Int {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
    }
    
    // Obter volume máximo
    fun getMaxVolume(): Int {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
    }
    
    // Definir volume
    fun setVolume(volume: Int): Boolean {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            val newVolume = if (volume > maxVolume) maxVolume else volume
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, newVolume, 0)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao definir volume: ${e.message}")
            return false
        }
    }
}
