package com.secureguard.server.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class LocationData(
    val id: String = UUID.randomUUID().toString(),
    val deviceId: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long = System.currentTimeMillis()
)
