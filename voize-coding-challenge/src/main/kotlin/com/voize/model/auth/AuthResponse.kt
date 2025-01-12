package com.voize.model.auth

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class AuthResponse(val token: String)