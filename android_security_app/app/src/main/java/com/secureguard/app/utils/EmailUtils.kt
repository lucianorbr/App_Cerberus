package com.secureguard.app.utils

import android.content.Context
import android.util.Log
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import java.io.File

class EmailUtils(private val context: Context) {
    
    private val TAG = "EmailUtils"
    private val preferencesManager = PreferencesManager(context)
    
    // Configurações de e-mail
    private val EMAIL_HOST = "smtp.gmail.com"
    private val EMAIL_PORT = "465"
    private val EMAIL_FROM = "secureguard.app@gmail.com"
    private val EMAIL_PASSWORD = "app_password" // Nota: Em produção, usar métodos seguros de armazenamento
    
    // Enviar e-mail simples
    fun sendEmail(subject: String, body: String): Boolean {
        val recipientEmail = preferencesManager.getNotificationEmail()
        
        if (recipientEmail.isEmpty()) {
            Log.e(TAG, "Não é possível enviar e-mail: e-mail de notificação não configurado")
            return false
        }
        
        return try {
            Thread {
                try {
                    val props = Properties()
                    props["mail.smtp.host"] = EMAIL_HOST
                    props["mail.smtp.socketFactory.port"] = EMAIL_PORT
                    props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
                    props["mail.smtp.auth"] = "true"
                    props["mail.smtp.port"] = EMAIL_PORT
                    
                    val session = Session.getDefaultInstance(props, object : Authenticator() {
                        override fun getPasswordAuthentication(): PasswordAuthentication {
                            return PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD)
                        }
                    })
                    
                    val message = MimeMessage(session)
                    message.setFrom(InternetAddress(EMAIL_FROM))
                    message.addRecipient(Message.RecipientType.TO, InternetAddress(recipientEmail))
                    message.subject = subject
                    message.setText(body)
                    
                    Transport.send(message)
                    Log.i(TAG, "E-mail enviado com sucesso para $recipientEmail")
                } catch (e: Exception) {
                    Log.e(TAG, "Erro ao enviar e-mail: ${e.message}")
                }
            }.start()
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao iniciar thread de envio de e-mail: ${e.message}")
            false
        }
    }
    
    // Enviar e-mail com anexo
    fun sendEmailWithAttachment(subject: String, body: String, attachment: File): Boolean {
        val recipientEmail = preferencesManager.getNotificationEmail()
        
        if (recipientEmail.isEmpty()) {
            Log.e(TAG, "Não é possível enviar e-mail: e-mail de notificação não configurado")
            return false
        }
        
        if (!attachment.exists()) {
            Log.e(TAG, "Não é possível enviar e-mail: arquivo anexo não existe")
            return false
        }
        
        return try {
            Thread {
                try {
                    val props = Properties()
                    props["mail.smtp.host"] = EMAIL_HOST
                    props["mail.smtp.socketFactory.port"] = EMAIL_PORT
                    props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
                    props["mail.smtp.auth"] = "true"
                    props["mail.smtp.port"] = EMAIL_PORT
                    
                    val session = Session.getDefaultInstance(props, object : Authenticator() {
                        override fun getPasswordAuthentication(): PasswordAuthentication {
                            return PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD)
                        }
                    })
                    
                    val message = MimeMessage(session)
                    message.setFrom(InternetAddress(EMAIL_FROM))
                    message.addRecipient(Message.RecipientType.TO, InternetAddress(recipientEmail))
                    message.subject = subject
                    
                    // Criar parte de texto
                    val textPart = MimeBodyPart()
                    textPart.setText(body)
                    
                    // Criar parte de anexo
                    val attachmentPart = MimeBodyPart()
                    attachmentPart.attachFile(attachment)
                    
                    // Criar multipart e adicionar partes
                    val multipart = MimeMultipart()
                    multipart.addBodyPart(textPart)
                    multipart.addBodyPart(attachmentPart)
                    
                    // Definir conteúdo da mensagem
                    message.setContent(multipart)
                    
                    Transport.send(message)
                    Log.i(TAG, "E-mail com anexo enviado com sucesso para $recipientEmail")
                } catch (e: Exception) {
                    Log.e(TAG, "Erro ao enviar e-mail com anexo: ${e.message}")
                }
            }.start()
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao iniciar thread de envio de e-mail com anexo: ${e.message}")
            false
        }
    }
    
    // Verificar se o e-mail de notificação está configurado
    fun isNotificationEmailConfigured(): Boolean {
        return preferencesManager.getNotificationEmail().isNotEmpty()
    }
}
