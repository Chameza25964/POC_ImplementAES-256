package com.example.aes256decryptor

import android.util.Base64
import java.lang.Exception
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

private val secretKey = "X+E7P#HkEj,Qytgn8Q"
private val salt = secretKey.toByteArray()
private val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

private val pbeKeySpec = PBEKeySpec(secretKey.toCharArray(), salt, 1000, 256)
private val keyBytes = secretKeyFactory.generateSecret(pbeKeySpec).encoded
private val keySpec = SecretKeySpec(keyBytes, "AES")

private val spec = PBEKeySpec(secretKey.toCharArray(), salt, 1000, 384)
private val b = secretKeyFactory.generateSecret(spec).encoded
private val ivBytes = ByteArray(16)

fun decryptData(encryptedData: String): String {
    try {
        System.arraycopy(b, 32, ivBytes, 0, 16)

        val ivSpec = IvParameterSpec(ivBytes)
        //val cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING")
        var dataToDecrypted = encryptedData.replace("_", "/").replace("-", "+")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        when(dataToDecrypted.length % 4) {
            2 -> dataToDecrypted += "=="
            3 -> dataToDecrypted += "="
        }

        val digest = MessageDigest.getInstance("SHA-256")
        val hashedString = String(digest.digest(salt), Charsets.UTF_8)
        val hashedSaltSize = getSaltSize(digest.digest(salt))

        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

        val decryptedBytes = cipher.doFinal(Base64.decode(dataToDecrypted, Base64.DEFAULT))
        val originalBytes = ByteArray(decryptedBytes.size - hashedSaltSize)

        for (i in hashedSaltSize until decryptedBytes.size) {
            originalBytes[i - hashedSaltSize] = decryptedBytes[i]
        }

        return String(originalBytes, Charsets.UTF_8)
    } catch (ex: Exception) {
        return ex.message!!
    }
}

private fun getSaltSize(hashedSaltBytes: ByteArray): Int {
    val key = PBEKeySpec(String(hashedSaltBytes).toCharArray(), hashedSaltBytes, 1000, 16)
    val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    val keyBytes = secretKeyFactory.generateSecret(key).encoded
    val ba = ByteArray(2)

    System.arraycopy(keyBytes, 0, ba, 0, 2)

    var saltSize = 0
    ba.forEach {
        saltSize += it.toString().map { it.toString().toInt() }.sumBy { it }
    }

    return saltSize
}