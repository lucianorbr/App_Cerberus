package com.secureguard.app.utils

import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.secureguard.app.models.LocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class LocationUtils(private val context: Context) {
    
    private val TAG = "LocationUtils"
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val preferencesManager = PreferencesManager(context)
    private val serverApi = ServerApi()
    
    // Obter localização atual
    suspend fun getCurrentLocation(): Location? {
        return try {
            withContext(Dispatchers.IO) {
                val locationTask = fusedLocationClient.lastLocation
                val location = locationTask.await()
                
                if (location != null) {
                    // Enviar localização para o servidor
                    sendLocationToServer(location)
                }
                
                location
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Erro ao obter localização: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao obter localização: ${e.message}")
            null
        }
    }
    
    // Enviar localização para o servidor
    private fun sendLocationToServer(location: Location) {
        val deviceId = preferencesManager.getDeviceId()
        
        serverApi.sendLocationUpdate(
            deviceId,
            location.latitude,
            location.longitude,
            location.accuracy,
            location.time
        )
        
        Log.i(TAG, "Localização enviada para o servidor: ${location.latitude}, ${location.longitude}")
    }
    
    // Obter histórico de localização (simulado por enquanto)
    fun getLocationHistory(): List<LatLng> {
        // Em uma implementação real, isso seria obtido do servidor
        // Por enquanto, vamos retornar dados simulados
        return listOf(
            LatLng(-23.550520, -46.633308), // São Paulo
            LatLng(-23.551000, -46.634000),
            LatLng(-23.552000, -46.635000),
            LatLng(-23.553000, -46.636000)
        )
    }
    
    // Formatar data de última atualização
    fun formatLastUpdateTime(timestamp: Long): String {
        val date = Date(timestamp)
        val format = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault())
        return "Última atualização: ${format.format(date)}"
    }
    
    // Verificar se a localização está ativada
    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }
    
    // Calcular distância entre duas localizações
    fun calculateDistance(start: LatLng, end: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            start.latitude, start.longitude,
            end.latitude, end.longitude,
            results
        )
        return results[0]
    }
    
    // Calcular distância total do percurso
    fun calculateTotalDistance(points: List<LatLng>): Float {
        var totalDistance = 0f
        
        for (i in 0 until points.size - 1) {
            totalDistance += calculateDistance(points[i], points[i + 1])
        }
        
        return totalDistance
    }
    
    // Suspender função para aguardar resultado de Task
    private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T? {
        return withContext(Dispatchers.IO) {
            try {
                if (isComplete) {
                    if (isSuccessful) {
                        result
                    } else {
                        null
                    }
                } else {
                    val latch = java.util.concurrent.CountDownLatch(1)
                    addOnCompleteListener {
                        latch.countDown()
                    }
                    latch.await()
                    if (isSuccessful) {
                        result
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
