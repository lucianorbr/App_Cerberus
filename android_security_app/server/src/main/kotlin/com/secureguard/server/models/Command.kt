package com.secureguard.server.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Command(
    val id: String = UUID.randomUUID().toString(),
    val deviceId: String,
    val type: CommandType,
    val parameters: Map<String, String> = mapOf(),
    val timestamp: Long = System.currentTimeMillis(),
    val status: CommandStatus = CommandStatus.PENDING
)

@Serializable
enum class CommandType {
    LOCK_DEVICE,
    RESET_PASSWORD,
    SOUND_ALERT,
    STOP_SOUND_ALERT,
    MAKE_CALL,
    TAKE_PHOTO,
    UPDATE_SETTINGS,
    WIPE_DATA
}

@Serializable
enum class CommandStatus {
    PENDING,
    SENT,
    DELIVERED,
    EXECUTED,
    FAILED
}
