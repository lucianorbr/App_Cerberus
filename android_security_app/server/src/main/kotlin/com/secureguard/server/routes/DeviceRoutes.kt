package com.secureguard.server.routes

import com.secureguard.server.models.Device
import com.secureguard.server.models.LocationData
import com.secureguard.server.models.User
import com.secureguard.server.services.DeviceService
import com.secureguard.server.services.LocationService
import com.secureguard.server.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.deviceRoutes(
    deviceService: DeviceService,
    locationService: LocationService,
    userService: UserService
) {
    route("/api/devices") {
        // Rota para registrar um novo dispositivo
        post {
            try {
                val device = call.receive<Device>()
                val savedDevice = deviceService.registerDevice(device)
                call.respond(HttpStatusCode.Created, savedDevice)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Rotas protegidas por autenticação
        authenticate("auth-jwt") {
            // Obter todos os dispositivos do usuário
            get {
                try {
                    val userId = call.principal<User>()?.id ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val devices = deviceService.getDevicesByUserId(userId)
                    call.respond(devices)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }

            // Obter um dispositivo específico
            get("/{id}") {
                try {
                    val userId = call.principal<User>()?.id ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val deviceId = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    
                    val device = deviceService.getDeviceById(deviceId)
                    if (device == null || device.userId != userId) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    
                    call.respond(device)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }

            // Atualizar um dispositivo
            put("/{id}") {
                try {
                    val userId = call.principal<User>()?.id ?: return@put call.respond(HttpStatusCode.Unauthorized)
                    val deviceId = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                    val deviceUpdate = call.receive<Device>()
                    
                    val existingDevice = deviceService.getDeviceById(deviceId)
                    if (existingDevice == null || existingDevice.userId != userId) {
                        call.respond(HttpStatusCode.NotFound)
                        return@put
                    }
                    
                    val updatedDevice = deviceService.updateDevice(deviceId, deviceUpdate)
                    call.respond(updatedDevice)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }

            // Excluir um dispositivo
            delete("/{id}") {
                try {
                    val userId = call.principal<User>()?.id ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                    val deviceId = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    
                    val existingDevice = deviceService.getDeviceById(deviceId)
                    if (existingDevice == null || existingDevice.userId != userId) {
                        call.respond(HttpStatusCode.NotFound)
                        return@delete
                    }
                    
                    deviceService.deleteDevice(deviceId)
                    call.respond(HttpStatusCode.NoContent)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }

            // Obter histórico de localização de um dispositivo
            get("/{id}/locations") {
                try {
                    val userId = call.principal<User>()?.id ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val deviceId = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    
                    val device = deviceService.getDeviceById(deviceId)
                    if (device == null || device.userId != userId) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    
                    val locations = locationService.getLocationsByDeviceId(deviceId)
                    call.respond(locations)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }

            // Enviar comando para o dispositivo
            post("/{id}/commands") {
                try {
                    val userId = call.principal<User>()?.id ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val deviceId = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val command = call.receive<Map<String, String>>()
                    
                    val device = deviceService.getDeviceById(deviceId)
                    if (device == null || device.userId != userId) {
                        call.respond(HttpStatusCode.NotFound)
                        return@post
                    }
                    
                    val result = deviceService.sendCommandToDevice(deviceId, command)
                    call.respond(mapOf("success" to result))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }
        }
    }

    // Rota para receber atualizações de localização dos dispositivos
    post("/api/locations") {
        try {
            val locationData = call.receive<LocationData>()
            locationService.saveLocation(locationData)
            call.respond(HttpStatusCode.Created)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        }
    }
}
