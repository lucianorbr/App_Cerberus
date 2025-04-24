package com.secureguard.server

import com.secureguard.server.routes.deviceRoutes
import com.secureguard.server.routes.userRoutes
import com.secureguard.server.services.DeviceService
import com.secureguard.server.services.LocationService
import com.secureguard.server.services.UserService
import com.secureguard.server.utils.FirebaseInitializer
import com.secureguard.server.utils.JwtProvider
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database

fun main() {
    // Configurações do servidor
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val jwtSecret = System.getenv("JWT_SECRET") ?: "secureguard_secret_key"
    val jwtIssuer = System.getenv("JWT_ISSUER") ?: "secureguard.app"
    val jwtAudience = System.getenv("JWT_AUDIENCE") ?: "secureguard.app"
    val dbUrl = System.getenv("DATABASE_URL") ?: "jdbc:h2:mem:secureguard;DB_CLOSE_DELAY=-1"
    val dbUser = System.getenv("DATABASE_USER") ?: ""
    val dbPassword = System.getenv("DATABASE_PASSWORD") ?: ""
    val firebaseConfigPath = System.getenv("FIREBASE_CONFIG_PATH") ?: "firebase-service-account.json"
    
    // Inicializar banco de dados
    val database = Database.connect(dbUrl, user = dbUser, password = dbPassword)
    
    // Inicializar Firebase
    try {
        FirebaseInitializer.initialize(firebaseConfigPath)
    } catch (e: Exception) {
        println("Aviso: Firebase não inicializado. Mensagens FCM não serão enviadas.")
        println("Erro: ${e.message}")
    }
    
    // Inicializar serviços
    val jwtProvider = JwtProvider(jwtSecret, jwtIssuer, jwtAudience)
    val userService = UserService(database, jwtProvider)
    val deviceService = DeviceService(database)
    val locationService = LocationService(database)
    
    // Iniciar servidor
    embeddedServer(Netty, port = port) {
        // Configurar plugins
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        
        install(CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowHeader(HttpHeaders.Authorization)
            allowHeader(HttpHeaders.ContentType)
            anyHost()
        }
        
        // Configurar autenticação JWT
        install(Authentication) {
            jwt("auth-jwt") {
                realm = "secureguard.app"
                verifier {
                    com.auth0.jwt.JWT
                        .require(com.auth0.jwt.algorithms.Algorithm.HMAC256(jwtSecret))
                        .withIssuer(jwtIssuer)
                        .withAudience(jwtAudience)
                        .build()
                }
                validate { credential ->
                    try {
                        val userId = credential.payload.getClaim("userId").asString()
                        val email = credential.payload.getClaim("email").asString()
                        if (userId.isNotEmpty() && email.isNotEmpty()) {
                            com.secureguard.server.models.User(id = userId, name = "", email = email, password = "")
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }
                }
                challenge { _, _ ->
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token inválido ou expirado"))
                }
            }
        }
        
        // Configurar rotas
        routing {
            get("/") {
                call.respond(mapOf("status" to "online", "message" to "SecureGuard API"))
            }
            
            // Registrar rotas de usuários e dispositivos
            userRoutes(userService)
            deviceRoutes(deviceService, locationService, userService)
        }
    }.start(wait = true)
}
