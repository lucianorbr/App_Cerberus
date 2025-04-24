package com.secureguard.server.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {
    
    // Hash de senha
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
    
    // Verificar senha
    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }
}
