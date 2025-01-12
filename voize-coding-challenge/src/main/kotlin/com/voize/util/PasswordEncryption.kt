package com.voize.util

import io.ktor.util.*
import io.ktor.utils.io.core.*
import java.security.KeyFactory
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

object PasswordEncryption {
    private val oaepParams = OAEPParameterSpec(
        "SHA-256",
        "MGF1",
        MGF1ParameterSpec.SHA256,
        PSource.PSpecified.DEFAULT
    )

    private val keyFactory = KeyFactory.getInstance("RSA")

    fun encryptPassword(password: String, publicKey: String): String {
        val key = publicKey.decodeBase64Bytes()
            .let(::X509EncodedKeySpec)
            .let(keyFactory::generatePublic)

        return Cipher.getInstance("RSA/ECB/OAEPPadding").run {
            init(Cipher.ENCRYPT_MODE, key, oaepParams)
            doFinal(password.toByteArray())
        }.encodeBase64()
    }
}