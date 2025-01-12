package com.voize.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeyResponse (
    @SerialName("Key")
    val key: String,
)