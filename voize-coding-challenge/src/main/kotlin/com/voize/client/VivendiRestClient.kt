package com.voize.client

import com.voize.model.auth.AuthRequest
import com.voize.model.auth.AuthResponse
import com.voize.model.auth.ErrorResponse
import com.voize.model.auth.KeyResponse
import com.voize.util.PasswordEncryption
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.io.IOException
import org.slf4j.LoggerFactory

class VivendiRestClient(
    private val baseUrl: String = "https://vivapp.vivendi.de:4482/api/vivendi/v1",
    private val httpClient: HttpClient
) {
    private val logger = LoggerFactory.getLogger(VivendiRestClient::class.java)

    suspend fun authenticate(username: String, password: String): Result<String> {
        return runCatching {
            val publicKey = httpClient.get("$baseUrl/auth/rsa-key").body<KeyResponse>().key
            val encryptedPassword = PasswordEncryption.encryptPassword(password, publicKey)

            val response = httpClient.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(
                    username = username,
                    password = encryptedPassword,
                    publicKey = publicKey
                ))
            }

            when (response.status) {
                HttpStatusCode.OK -> response.body<AuthResponse>().token
                HttpStatusCode.Unauthorized -> {
                    val error = response.body<ErrorResponse>()
                    throw IllegalArgumentException(error.Message)
                }
                else -> throw IOException("Server error: ${response.status}")
            }
        }.onSuccess {
            logger.info("Successfully authenticated")
        }.onFailure { e ->
            logger.error("Authentication error for user $username: ${e.message}")
        }
    }
}