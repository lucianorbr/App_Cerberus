package com.secureguard.server.services

import com.secureguard.server.models.User
import com.secureguard.server.utils.JwtProvider
import com.secureguard.server.utils.PasswordHasher
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class UserService(private val database: Database, private val jwtProvider: JwtProvider) {
    
    // Tabela de usuários
    private object Users : Table() {
        val id = varchar("id", 36).primaryKey()
        val name = varchar("name", 100)
        val email = varchar("email", 100).uniqueIndex()
        val password = varchar("password", 255)
        val createdAt = long("created_at")
    }
    
    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }
    
    // Registrar um novo usuário
    fun registerUser(user: User): User? {
        return transaction(database) {
            // Verificar se o e-mail já existe
            val existingUser = Users.select { Users.email eq user.email }.singleOrNull()
            if (existingUser != null) {
                return@transaction null
            }
            
            // Hash da senha
            val hashedPassword = PasswordHasher.hashPassword(user.password)
            
            // Inserir novo usuário
            val userId = UUID.randomUUID().toString()
            Users.insert {
                it[id] = userId
                it[name] = user.name
                it[email] = user.email
                it[password] = hashedPassword
                it[createdAt] = System.currentTimeMillis()
            }
            
            // Retornar usuário criado
            user.copy(id = userId, password = "")
        }
    }
    
    // Login de usuário
    fun loginUser(email: String, password: String): String? {
        return transaction(database) {
            val user = Users.select { Users.email eq email }.singleOrNull() ?: return@transaction null
            
            val storedPassword = user[Users.password]
            if (!PasswordHasher.verifyPassword(password, storedPassword)) {
                return@transaction null
            }
            
            // Gerar token JWT
            jwtProvider.generateToken(user[Users.id], user[Users.email])
        }
    }
    
    // Obter usuário por ID
    fun getUserById(userId: String): User? {
        return transaction(database) {
            val user = Users.select { Users.id eq userId }.singleOrNull() ?: return@transaction null
            
            User(
                id = user[Users.id],
                name = user[Users.name],
                email = user[Users.email],
                password = "",
                createdAt = user[Users.createdAt]
            )
        }
    }
    
    // Atualizar usuário
    fun updateUser(userId: String, userUpdate: User): User? {
        return transaction(database) {
            val user = Users.select { Users.id eq userId }.singleOrNull() ?: return@transaction null
            
            Users.update({ Users.id eq userId }) {
                it[name] = userUpdate.name
                // Não atualizamos o e-mail e a senha aqui
            }
            
            User(
                id = user[Users.id],
                name = userUpdate.name,
                email = user[Users.email],
                password = "",
                createdAt = user[Users.createdAt]
            )
        }
    }
    
    // Alterar senha
    fun changePassword(userId: String, currentPassword: String, newPassword: String): Boolean {
        return transaction(database) {
            val user = Users.select { Users.id eq userId }.singleOrNull() ?: return@transaction false
            
            val storedPassword = user[Users.password]
            if (!PasswordHasher.verifyPassword(currentPassword, storedPassword)) {
                return@transaction false
            }
            
            val hashedNewPassword = PasswordHasher.hashPassword(newPassword)
            Users.update({ Users.id eq userId }) {
                it[password] = hashedNewPassword
            }
            
            true
        }
    }
}
