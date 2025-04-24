package com.secureguard.server.services

import com.secureguard.server.models.LocationData
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class LocationService(private val database: Database) {
    
    // Tabela de localizações
    private object Locations : Table() {
        val id = varchar("id", 36).primaryKey()
        val deviceId = varchar("device_id", 36).references(DeviceService.Devices.id)
        val latitude = double("latitude")
        val longitude = double("longitude")
        val accuracy = float("accuracy")
        val timestamp = long("timestamp")
    }
    
    init {
        transaction(database) {
            SchemaUtils.create(Locations)
        }
    }
    
    // Salvar nova localização
    fun saveLocation(locationData: LocationData): LocationData {
        return transaction(database) {
            val locationId = UUID.randomUUID().toString()
            
            Locations.insert {
                it[id] = locationId
                it[deviceId] = locationData.deviceId
                it[latitude] = locationData.latitude
                it[longitude] = locationData.longitude
                it[accuracy] = locationData.accuracy
                it[timestamp] = locationData.timestamp
            }
            
            // Atualizar timestamp de última visualização do dispositivo
            DeviceService.Devices.update({ DeviceService.Devices.deviceId eq locationData.deviceId }) {
                it[lastSeen] = System.currentTimeMillis()
            }
            
            locationData.copy(id = locationId)
        }
    }
    
    // Obter localizações por ID de dispositivo
    fun getLocationsByDeviceId(deviceId: String, limit: Int = 100): List<LocationData> {
        return transaction(database) {
            Locations.select { Locations.deviceId eq deviceId }
                .orderBy(Locations.timestamp, SortOrder.DESC)
                .limit(limit)
                .map {
                    LocationData(
                        id = it[Locations.id],
                        deviceId = it[Locations.deviceId],
                        latitude = it[Locations.latitude],
                        longitude = it[Locations.longitude],
                        accuracy = it[Locations.accuracy],
                        timestamp = it[Locations.timestamp]
                    )
                }
        }
    }
    
    // Obter última localização de um dispositivo
    fun getLastLocationByDeviceId(deviceId: String): LocationData? {
        return transaction(database) {
            Locations.select { Locations.deviceId eq deviceId }
                .orderBy(Locations.timestamp, SortOrder.DESC)
                .limit(1)
                .singleOrNull()
                ?.let {
                    LocationData(
                        id = it[Locations.id],
                        deviceId = it[Locations.deviceId],
                        latitude = it[Locations.latitude],
                        longitude = it[Locations.longitude],
                        accuracy = it[Locations.accuracy],
                        timestamp = it[Locations.timestamp]
                    )
                }
        }
    }
    
    // Excluir localizações antigas de um dispositivo
    fun deleteOldLocations(deviceId: String, olderThanTimestamp: Long): Int {
        return transaction(database) {
            Locations.deleteWhere { 
                (Locations.deviceId eq deviceId) and (Locations.timestamp less olderThanTimestamp)
            }
        }
    }
}
