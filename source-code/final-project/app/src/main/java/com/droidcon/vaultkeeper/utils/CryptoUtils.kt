package com.droidcon.vaultkeeper.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class CryptoUtils {
    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val TRANSFORMATION = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}"
        private const val IV_SEPARATOR = ";"
        private const val TAG_LENGTH = 128 // GCM authentication tag length in bits
        
        // Key alias for the VaultKeeper app
        private const val KEY_ALIAS = "VAULT_KEEPER_KEY"
        
        /**
         * Generates or retrieves a secret key for encryption
         */
        private fun getOrCreateSecretKey(keyAlias: String = KEY_ALIAS): SecretKey {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            
            if (keyStore.containsAlias(keyAlias)) {
                // Key exists, retrieve it
                val entry = keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry
                return entry.secretKey
            }
            
            // Generate a new key with enhanced security features
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )
            
            val keyGenParams = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setUserAuthenticationRequired(false) // Set to true to require auth for key use
                .build()
            
            keyGenerator.init(keyGenParams)
            return keyGenerator.generateKey()
        }
        
        /**
         * Encrypts a string and returns the encrypted string
         * Format: base64(iv);base64(encryptedData)
         */
        fun encrypt(plainText: String): String {
            val secretKey = getOrCreateSecretKey()
            
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            // Save IV for decryption
            val iv = cipher.iv
            val ivString = Base64.encodeToString(iv, Base64.DEFAULT)
            
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            val encryptedString = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
            
            // Return IV and encrypted data together
            return "$ivString$IV_SEPARATOR$encryptedString"
        }
        
        /**
         * Decrypts a string that was encrypted with the encrypt method
         */
        fun decrypt(encryptedText: String): String {
            try {
                val secretKey = getOrCreateSecretKey()
                
                // Split the IV and encrypted data
                val split = encryptedText.split(IV_SEPARATOR)
                if (split.size != 2) throw IllegalArgumentException("Invalid encrypted data format")
                
                val ivString = split[0]
                val encryptedDataString = split[1]
                
                val iv = Base64.decode(ivString, Base64.DEFAULT)
                val encryptedData = Base64.decode(encryptedDataString, Base64.DEFAULT)
                
                // Initialize cipher for decryption with the saved IV
                val cipher = Cipher.getInstance(TRANSFORMATION)
                val spec = GCMParameterSpec(TAG_LENGTH, iv)
                cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
                
                val decryptedBytes = cipher.doFinal(encryptedData)
                return String(decryptedBytes, Charsets.UTF_8)
            } catch (e: Exception) {
                throw RuntimeException("Decryption error: ${e.message}", e)
            }
        }
        
        /**
         * Creates a CipherOutputStream that encrypts data
         */
        fun encryptStream(outputStream: OutputStream): Pair<CipherOutputStream, ByteArray> {
            val secretKey = getOrCreateSecretKey()
            
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            // Save IV for later decryption (the caller must store this)
            val iv = cipher.iv
            
            // Return the stream and IV
            return Pair(CipherOutputStream(outputStream, cipher), iv)
        }
        
        /**
         * Creates a CipherInputStream that decrypts data
         */
        fun decryptStream(inputStream: InputStream, iv: ByteArray): CipherInputStream {
            val secretKey = getOrCreateSecretKey()
            
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            
            return CipherInputStream(inputStream, cipher)
        }
    }
} 