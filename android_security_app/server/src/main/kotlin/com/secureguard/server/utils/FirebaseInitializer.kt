package com.secureguard.server.utils

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import java.io.FileInputStream

object FirebaseInitializer {
    
    // Inicializar Firebase
    fun initialize(serviceAccountPath: String) {
        try {
            val serviceAccount = FileInputStream(serviceAccountPath)
            
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()
            
            FirebaseApp.initializeApp(options)
        } catch (e: Exception) {
            throw RuntimeException("Erro ao inicializar Firebase: ${e.message}")
        }
    }
    
    // Enviar mensagem FCM
    fun sendMessage(token: String, data: Map<String, String>): String {
        val message = com.google.firebase.messaging.Message.builder()
            .setToken(token)
            .putAllData(data)
            .build()
        
        return FirebaseMessaging.getInstance().send(message)
    }
}
