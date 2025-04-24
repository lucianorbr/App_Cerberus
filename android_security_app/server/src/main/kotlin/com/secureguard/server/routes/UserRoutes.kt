package com.secureguard.server.routes

import com.secureguard.server.models.User
import com.secureguard.server.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(userService: UserService) {
    route("/api/users") {
        // Rota para registro de usuário
        post("/register") {
            try {
                val user = call.receive<User>()
                val result = userService.registerUser(user)
                if (result != null) {
                    call.respond(HttpStatusCode.Created, result)
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Usuário já existe"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Rota para login
        post("/login") {
            try {
                val credentials = call.receive<Map<String, String>>()
                val email = credentials["email"] ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Email é obrigatório"))
                val password = credentials["password"] ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Senha é obrigatória"))
                
                val token = userService.loginUser(email, password)
                if (token != null) {
                    call.respond(mapOf("token" to token))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Credenciais inválidas"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Rotas protegidas por autenticação
        authenticate("auth-jwt") {
            // Obter perfil do usuário
            get("/profile") {
                try {
                    val userId = call.principal<User>()?.id ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val user = userService.getUserById(userId)
                    if (user != null) {
                        // Remover senha antes de enviar
                        val userWithoutPassword = user.copy(password = "")
                        call.respond(userWithoutPassword)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }

            // Atualizar perfil do usuário
            put("/profile") {
                try {
                    val userId = call.principal<User>()?.id ?: return@put call.respond(HttpStatusCode.Unauthorized)
                    val userUpdate = call.receive<User>()
                    
                    val updatedUser = userService.updateUser(userId, userUpdate)
                    if (updatedUser != null) {
                        // Remover senha antes de enviar
                        val userWithoutPassword = updatedUser.copy(password = "")
                        call.respond(userWithoutPassword)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }

            // Alterar senha
            post("/change-password") {
                try {
                    val userId = call.principal<User>()?.id ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val passwordData = call.receive<Map<String, String>>()
                    
                    val currentPassword = passwordData["currentPassword"] ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Senha atual é obrigatória"))
                    val newPassword = passwordData["newPassword"] ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Nova senha é obrigatória"))
                    
                    val success = userService.changePassword(userId, currentPassword, newPassword)
                    if (success) {
                        call.respond(mapOf("success" to true))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Senha atual incorreta"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }
        }
    }
}
