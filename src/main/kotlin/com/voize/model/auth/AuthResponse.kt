package com.voize.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@JvmInline
value class AuthToken(val token: String)

@Serializable
data class ErrorResponse(
    val message: String? = null,
    val error: String? = null,
    val errorDescription: String? = null
) {
    fun getErrorMessage(): String {
        return message ?: error ?: errorDescription ?: "Unknown error occurred"
    }
}