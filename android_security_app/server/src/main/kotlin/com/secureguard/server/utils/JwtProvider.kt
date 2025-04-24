package com.secureguard.server.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JwtProvider(private val secret: String, private val issuer: String, private val audience: String) {
    
    private val algorithm = Algorithm.HMAC256(secret)
    
    // Gerar token JWT
    fun generateToken(userId: String, email: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
            .sign(algorithm)
    }
    
    // Verificar token JWT
    fun verifyToken(token: String): Map<String, String> {
        val verifier = JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .build()
        
        val decodedJWT = verifier.verify(token)
        
        return mapOf(
            "userId" to decodedJWT.getClaim("userId").asString(),
            "email" to decodedJWT.getClaim("email").asString()
        )
    }
    
    companion object {
        // Token expira em 7 dias
        private const val TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000L
    }
}
