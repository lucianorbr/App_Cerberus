package com.secureguard.app.utils

import android.util.Log
import com.secureguard.app.models.LocationData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

class ServerApi {

    private val TAG = "ServerApi"
    private val BASE_URL = "https://secureguard-server.com/api/"
    private lateinit var apiService: ApiService

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    // Enviar atualização de localização para o servidor
    fun sendLocationUpdate(deviceId: String, latitude: Double, longitude: Double, accuracy: Float, timestamp: Long) {
        try {
            val locationData = LocationData(
                deviceId = deviceId,
                latitude = latitude,
                longitude = longitude,
                accuracy = accuracy,
                timestamp = timestamp
            )

            apiService.sendLocationUpdate(locationData).enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                    if (response.isSuccessful) {
                        Log.i(TAG, "Localização enviada com sucesso para o servidor")
                    } else {
                        Log.e(TAG, "Erro ao enviar localização: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                    Log.e(TAG, "Falha ao enviar localização: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao enviar localização: ${e.message}")
        }
    }

    // Registrar dispositivo no servidor
    fun registerDevice(deviceId: String, fcmToken: String, email: String) {
        try {
            val deviceData = mapOf(
                "deviceId" to deviceId,
                "fcmToken" to fcmToken,
                "email" to email
            )

            apiService.registerDevice(deviceData).enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                    if (response.isSuccessful) {
                        Log.i(TAG, "Dispositivo registrado com sucesso no servidor")
                    } else {
                        Log.e(TAG, "Erro ao registrar dispositivo: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                    Log.e(TAG, "Falha ao registrar dispositivo: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao registrar dispositivo: ${e.message}")
        }
    }

    // Interface para definir endpoints da API
    interface ApiService {
        @POST("location")
        fun sendLocationUpdate(@Body locationData: LocationData): retrofit2.Call<Void>

        @POST("devices")
        fun registerDevice(@Body deviceData: Map<String, String>): retrofit2.Call<Void>

        @GET("devices/{deviceId}")
        fun getDeviceInfo(@Path("deviceId") deviceId: String): retrofit2.Call<Map<String, Any>>
    }
}
