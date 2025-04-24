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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.secureguard.app.R
import com.secureguard.app.utils.PreferencesManager
import com.secureguard.app.utils.ServerApi
import java.util.concurrent.TimeUnit

class LocationTrackingService : Service() {

    private val TAG = "LocationTrackingService"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var serverApi: ServerApi

    companion object {
        private const val NOTIFICATION_ID = 12345
        private const val CHANNEL_ID = "location_tracking_channel"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Serviço de rastreamento de localização criado")
        
        preferencesManager = PreferencesManager(this)
        serverApi = ServerApi()
        
        // Inicializar cliente de localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        // Configurar callback de localização
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.i(TAG, "Nova localização: ${location.latitude}, ${location.longitude}")
                    
                    // Enviar localização para o servidor
                    if (preferencesManager.isLocationTrackingEnabled()) {
                        serverApi.sendLocationUpdate(
                            preferencesManager.getDeviceId(),
                            location.latitude,
                            location.longitude,
                            location.accuracy,
                            location.time
                        )
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Serviço de rastreamento de localização iniciado")
        
        // Criar canal de notificação para Android 8.0+
        createNotificationChannel()
        
        // Iniciar como serviço em primeiro plano
        startForeground(NOTIFICATION_ID, createNotification())
        
        // Iniciar atualizações de localização se estiver habilitado
        if (preferencesManager.isLocationTrackingEnabled()) {
            startLocationUpdates()
        }
        
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Serviço de rastreamento de localização destruído")
        
        // Parar atualizações de localização
        stopLocationUpdates()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Rastreamento de Localização"
            val descriptionText = "Canal para notificações de rastreamento de localização"
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
            .setContentText("Rastreamento de localização ativo")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                TimeUnit.MINUTES.toMillis(15)
            ).build()
            
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                mainLooper
            )
            
            Log.i(TAG, "Atualizações de localização iniciadas")
        } catch (e: SecurityException) {
            Log.e(TAG, "Erro ao iniciar atualizações de localização: ${e.message}")
        }
    }
    
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.i(TAG, "Atualizações de localização paradas")
    }
}
