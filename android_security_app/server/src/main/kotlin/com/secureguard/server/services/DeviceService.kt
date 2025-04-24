package com.secureguard.server.services

import com.google.firebase.messaging.FirebaseMessaging
import com.secureguard.server.models.Command
import com.secureguard.server.models.CommandStatus
import com.secureguard.server.models.Device
import com.secureguard.server.models.DeviceSettings
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class DeviceService(private val database: Database) {
    
    // Tabela de dispositivos
    private object Devices : Table() {
        val id = varchar("id", 36).primaryKey()
        val userId = varchar("user_id", 36).references(UserService.Users.id)
        val name = varchar("name", 100)
        val deviceId = varchar("device_id", 100)
        val fcmToken = varchar("fcm_token", 255)
        val lastSeen = long("last_seen")
        val isActive = bool("is_active")
        
        // Configurações do dispositivo
        val locationTrackingEnabled = bool("location_tracking_enabled")
        val wrongPasswordPhotoEnabled = bool("wrong_password_photo_enabled")
        val simChangeDetectionEnabled = bool("sim_change_detection_enabled")
        val remoteLockEnabled = bool("remote_lock_enabled")
        val soundAlertEnabled = bool("sound_alert_enabled")
        val callControlEnabled = bool("call_control_enabled")
        val notificationEmail = varchar("notification_email", 100)
    }
    
    // Tabela de comandos
    private object Commands : Table() {
        val id = varchar("id", 36).primaryKey()
        val deviceId = varchar("device_id", 36).references(Devices.id)
        val type = varchar("type", 50)
        val parameters = text("parameters")
        val timestamp = long("timestamp")
        val status = varchar("status", 20)
    }
    
    init {
        transaction(database) {
            SchemaUtils.create(Devices, Commands)
        }
    }
    
    // Registrar um novo dispositivo
    fun registerDevice(device: Device): Device {
        return transaction(database) {
            // Verificar se o dispositivo já existe
            val existingDevice = Devices.select { Devices.deviceId eq device.deviceId }.singleOrNull()
            
            if (existingDevice != null) {
                // Atualizar token FCM e marcar como ativo
                Devices.update({ Devices.id eq existingDevice[Devices.id] }) {
                    it[fcmToken] = device.fcmToken
                    it[lastSeen] = System.currentTimeMillis()
                    it[isActive] = true
                }
                
                return@transaction getDeviceById(existingDevice[Devices.id])!!
            }
            
            // Inserir novo dispositivo
            val deviceId = UUID.randomUUID().toString()
            Devices.insert {
                it[id] = deviceId
                it[userId] = device.userId
                it[name] = device.name
                it[this.deviceId] = device.deviceId
                it[fcmToken] = device.fcmToken
                it[lastSeen] = System.currentTimeMillis()
                it[isActive] = true
                
                // Configurações
                it[locationTrackingEnabled] = device.settings.locationTrackingEnabled
                it[wrongPasswordPhotoEnabled] = device.settings.wrongPasswordPhotoEnabled
                it[simChangeDetectionEnabled] = device.settings.simChangeDetectionEnabled
                it[remoteLockEnabled] = device.settings.remoteLockEnabled
                it[soundAlertEnabled] = device.settings.soundAlertEnabled
                it[callControlEnabled] = device.settings.callControlEnabled
                it[notificationEmail] = device.settings.notificationEmail
            }
            
            device.copy(id = deviceId)
        }
    }
    
    // Obter dispositivo por ID
    fun getDeviceById(deviceId: String): Device? {
        return transaction(database) {
            val device = Devices.select { Devices.id eq deviceId }.singleOrNull() ?: return@transaction null
            
            Device(
                id = device[Devices.id],
                userId = device[Devices.userId],
                name = device[Devices.name],
                deviceId = device[Devices.deviceId],
                fcmToken = device[Devices.fcmToken],
                lastSeen = device[Devices.lastSeen],
                isActive = device[Devices.isActive],
                settings = DeviceSettings(
                    locationTrackingEnabled = device[Devices.locationTrackingEnabled],
                    wrongPasswordPhotoEnabled = device[Devices.wrongPasswordPhotoEnabled],
                    simChangeDetectionEnabled = device[Devices.simChangeDetectionEnabled],
                    remoteLockEnabled = device[Devices.remoteLockEnabled],
                    soundAlertEnabled = device[Devices.soundAlertEnabled],
                    callControlEnabled = device[Devices.callControlEnabled],
                    notificationEmail = device[Devices.notificationEmail]
                )
            )
        }
    }
    
    // Obter dispositivos por ID de usuário
    fun getDevicesByUserId(userId: String): List<Device> {
        return transaction(database) {
            Devices.select { Devices.userId eq userId }
                .map {
                    Device(
                        id = it[Devices.id],
                        userId = it[Devices.userId],
                        name = it[Devices.name],
                        deviceId = it[Devices.deviceId],
                        fcmToken = it[Devices.fcmToken],
                        lastSeen = it[Devices.lastSeen],
                        isActive = it[Devices.isActive],
                        settings = DeviceSettings(
                            locationTrackingEnabled = it[Devices.locationTrackingEnabled],
                            wrongPasswordPhotoEnabled = it[Devices.wrongPasswordPhotoEnabled],
                            simChangeDetectionEnabled = it[Devices.simChangeDetectionEnabled],
                            remoteLockEnabled = it[Devices.remoteLockEnabled],
                            soundAlertEnabled = it[Devices.soundAlertEnabled],
                            callControlEnabled = it[Devices.callControlEnabled],
                            notificationEmail = it[Devices.notificationEmail]
                        )
                    )
                }
        }
    }
    
    // Atualizar dispositivo
    fun updateDevice(deviceId: String, deviceUpdate: Device): Device {
        return transaction(database) {
            Devices.update({ Devices.id eq deviceId }) {
                it[name] = deviceUpdate.name
                it[fcmToken] = deviceUpdate.fcmToken
                it[lastSeen] = System.currentTimeMillis()
                
                // Configurações
                it[locationTrackingEnabled] = deviceUpdate.settings.locationTrackingEnabled
                it[wrongPasswordPhotoEnabled] = deviceUpdate.settings.wrongPasswordPhotoEnabled
                it[simChangeDetectionEnabled] = deviceUpdate.settings.simChangeDetectionEnabled
                it[remoteLockEnabled] = deviceUpdate.settings.remoteLockEnabled
                it[soundAlertEnabled] = deviceUpdate.settings.soundAlertEnabled
                it[callControlEnabled] = deviceUpdate.settings.callControlEnabled
                it[notificationEmail] = deviceUpdate.settings.notificationEmail
            }
            
            getDeviceById(deviceId)!!
        }
    }
    
    // Excluir dispositivo
    fun deleteDevice(deviceId: String) {
        transaction(database) {
            // Primeiro excluir comandos relacionados
            Commands.deleteWhere { Commands.deviceId eq deviceId }
            
            // Depois excluir o dispositivo
            Devices.deleteWhere { Devices.id eq deviceId }
        }
    }
    
    // Enviar comando para o dispositivo
    fun sendCommandToDevice(deviceId: String, commandData: Map<String, String>): Boolean {
        return transaction(database) {
            val device = getDeviceById(deviceId) ?: return@transaction false
            
            val commandType = commandData["command"] ?: return@transaction false
            val commandId = UUID.randomUUID().toString()
            
            // Salvar comando no banco de dados
            Commands.insert {
                it[id] = commandId
                it[this.deviceId] = deviceId
                it[type] = commandType
                it[parameters] = commandData.filterKeys { key -> key != "command" }.toString()
                it[timestamp] = System.currentTimeMillis()
                it[status] = CommandStatus.PENDING.name
            }
            
            // Enviar comando via FCM
            try {
                val message = com.google.firebase.messaging.Message.builder()
                    .setToken(device.fcmToken)
                    .putAllData(commandData)
                    .putData("command_id", commandId)
                    .build()
                
                FirebaseMessaging.getInstance().send(message)
                
                // Atualizar status do comando
                Commands.update({ Commands.id eq commandId }) {
                    it[status] = CommandStatus.SENT.name
                }
                
                true
            } catch (e: Exception) {
                // Atualizar status do comando em caso de falha
                Commands.update({ Commands.id eq commandId }) {
                    it[status] = CommandStatus.FAILED.name
                }
                
                false
            }
        }
    }
    
    // Obter histórico de comandos de um dispositivo
    fun getCommandsByDeviceId(deviceId: String): List<Command> {
        return transaction(database) {
            Commands.select { Commands.deviceId eq deviceId }
                .orderBy(Commands.timestamp, SortOrder.DESC)
                .map {
                    Command(
                        id = it[Commands.id],
                        deviceId = it[Commands.deviceId],
                        type = com.secureguard.server.models.CommandType.valueOf(it[Commands.type]),
                        parameters = parseParameters(it[Commands.parameters]),
                        timestamp = it[Commands.timestamp],
                        status = CommandStatus.valueOf(it[Commands.status])
                    )
                }
        }
    }
    
    // Atualizar status de um comando
    fun updateCommandStatus(commandId: String, status: CommandStatus): Boolean {
        return transaction(database) {
            val command = Commands.select { Commands.id eq commandId }.singleOrNull() ?: return@transaction false
            
            Commands.update({ Commands.id eq commandId }) {
                it[Commands.status] = status.name
            }
            
            true
        }
    }
    
    // Auxiliar para converter string de parâmetros em mapa
    private fun parseParameters(parametersStr: String): Map<String, String> {
        if (parametersStr.isEmpty() || parametersStr == "{}") {
            return mapOf()
        }
        
        return try {
            // Implementação simples, poderia ser melhorada com uma biblioteca JSON
            parametersStr
                .trim('{', '}')
                .split(", ")
                .associate {
                    val parts = it.split("=")
                    parts[0] to parts[1]
                }
        } catch (e: Exception) {
            mapOf()
        }
    }
}
