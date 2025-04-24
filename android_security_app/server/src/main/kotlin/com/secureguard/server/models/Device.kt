package com.secureguard.server.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Device(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val name: String,
    val deviceId: String,
    val fcmToken: String,
    val lastSeen: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val settings: DeviceSettings = DeviceSettings()
)

@Serializable
data class DeviceSettings(
    val locationTrackingEnabled: Boolean = true,
    val wrongPasswordPhotoEnabled: Boolean = true,
    val simChangeDetectionEnabled: Boolean = true,
    val remoteLockEnabled: Boolean = true,
    val soundAlertEnabled: Boolean = true,
    val callControlEnabled: Boolean = true,
    val notificationEmail: String = ""
)
