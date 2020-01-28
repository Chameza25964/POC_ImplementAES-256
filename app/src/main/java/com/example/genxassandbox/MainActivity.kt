package com.example.genxassandbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.security.Key
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity() {

    lateinit var encryptedData: String
    lateinit var decryptedData: String
    val salt = ByteArray(64)
    val secretKey = "662ede816988e58fb6d057d9d85605e0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val random = SecureRandom()
        random.nextBytes(salt)
        val pbeKeySpec = PBEKeySpec(secretKey.toCharArray(), salt, 65536, 256)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = secretKeyFactory.generateSecret(pbeKeySpec).encoded
        val keySpec = SecretKeySpec(keyBytes, "AES")

        val ivRandom = SecureRandom()
        val initializeVectors = ByteArray(16)
        ivRandom.nextBytes(initializeVectors)
        val ivSpec = IvParameterSpec(initializeVectors)

        tvStartEncrypts.setOnClickListener {
            if (etCurrentPassword.text.isEmpty()) {
                return@setOnClickListener
            }
            val password = etCurrentPassword.text.toString()
            encryptedData = encryptPassword(password, keySpec, ivSpec)
            decryptedData = decryptPassword(keySpec, ivSpec)
            tvEncryptedPassword.text = encryptedData
            tvDecryptedPassword.text = decryptedData
        }
    }

    fun encryptPassword(password: String, keySpec: Key, ivSpec: IvParameterSpec): String {
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            return Base64.encodeToString(
                cipher.doFinal(password.toByteArray()),
                Base64.NO_PADDING
            )
        } catch (ex: Exception) {
            return ex.message!!
        }
    }

    fun decryptPassword(keySpec: Key, ivSpec: IvParameterSpec): String {
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING")
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            return String(cipher.doFinal(
                    Base64.decode(
                        encryptedData,
                        Base64.DEFAULT
                    )
                ))
        } catch (ex: Exception) {
            return ex.message!!
        }
    }
}
