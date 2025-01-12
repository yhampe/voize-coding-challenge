package com.voize.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val Message: String)