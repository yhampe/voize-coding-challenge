package com.voize.model.auth
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    @SerialName("Username")
    val username: String,

    @SerialName("Password")
    val password: String,

    @SerialName("PublicKey")
    val publicKey: String
)
