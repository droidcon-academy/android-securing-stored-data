package com.droidcon.vaultkeeper

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.MessageDigest
import java.security.SecureRandom
import androidx.core.content.edit

class SecurePinValidator(private val context: Context) {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val SALT_KEY_ALIAS = "PinValidatorSalt"
        private const val HASH_ALGORITHM = "SHA-256"
        private const val SALT_SIZE = 16 // 128 bits
    }

    private var storedHashedPin: String? = null

    init {
        // Ensure the salt exists when the validator is created
        ensureSaltExists()
    }

    // Create or retrieve the salt used for PIN hashing
    private fun ensureSaltExists() {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)

            if (!keyStore.containsAlias(SALT_KEY_ALIAS)) {
                // Generate a new salt and store it in the Android Keystore
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    ANDROID_KEYSTORE
                )

                val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                    SALT_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build()

                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()

                // Generate and store a random salt
                val salt = ByteArray(SALT_SIZE)
                SecureRandom().nextBytes(salt)

                // Encrypt and store the salt using our Keystore key
                val encryptedSalt = encryptSalt(salt)

                // Store the encrypted salt in SharedPreferences
                val prefs = context.getSharedPreferences("secure_pin_prefs", Context.MODE_PRIVATE)
                prefs.edit {
                    putString("salt", Base64.encodeToString(encryptedSalt, Base64.DEFAULT))
                }
            }
        } catch (e: Exception) {
            Log.e("SecurePinValidator", "Error ensuring salt exists", e)
            throw SecurityException("Could not initialize secure PIN validator", e)
        }
    }

    // Encrypt the salt using the Keystore key
    private fun encryptSalt(salt: ByteArray): ByteArray {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        val key = keyStore.getKey(SALT_KEY_ALIAS, null) as SecretKey

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val iv = cipher.iv
        val encryptedSalt = cipher.doFinal(salt)

        // Combine IV and encrypted salt
        val combined = ByteArray(iv.size + encryptedSalt.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedSalt, 0, combined, iv.size, encryptedSalt.size)

        return combined
    }

    // Decrypt the salt using the Keystore key
    private fun getSalt(): ByteArray {
        try {
            val prefs = context.getSharedPreferences("secure_pin_prefs", Context.MODE_PRIVATE)
            val encryptedSaltBase64 = prefs.getString("salt", null)
                ?: throw SecurityException("Salt not found")

            val combined = Base64.decode(encryptedSaltBase64, Base64.DEFAULT)

            // Extract IV (first 12 bytes for GCM)
            val iv = combined.copyOfRange(0, 12)
            val encryptedSalt = combined.copyOfRange(12, combined.size)

            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            val key = keyStore.getKey(SALT_KEY_ALIAS, null) as SecretKey

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)

            return cipher.doFinal(encryptedSalt)
        } catch (e: Exception) {
            Log.e("SecurePinValidator", "Error retrieving salt", e)
            throw SecurityException("Could not retrieve salt for PIN validation", e)
        }
    }

    // Hash a PIN with our secure salt
    private fun hashPin(pin: String): String {
        try {
            val salt = getSalt()

            // Combine PIN with salt
            val pinBytes = pin.toByteArray(Charset.forName("UTF-8"))
            val saltedInput = ByteArray(pinBytes.size + salt.size)
            System.arraycopy(pinBytes, 0, saltedInput, 0, pinBytes.size)
            System.arraycopy(salt, 0, saltedInput, pinBytes.size, salt.size)

            // Calculate hash
            val digest = MessageDigest.getInstance(HASH_ALGORITHM)
            val hashedBytes = digest.digest(saltedInput)

            // Convert to Base64 for storage
            return Base64.encodeToString(hashedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e("SecurePinValidator", "Error hashing PIN", e)
            throw SecurityException("Could not hash PIN", e)
        }
    }

    // Set a new PIN (stores only the hash)
    fun setPin(pin: String) {
        if (pin.length < 4) {
            throw IllegalArgumentException("PIN must be at least 4 digits")
        }

        // Hash the PIN with our salt
        storedHashedPin = hashPin(pin)

        // Save the hashed PIN
        val prefs = context.getSharedPreferences("secure_pin_prefs", Context.MODE_PRIVATE)
        prefs.edit { putString("hashed_pin", storedHashedPin) }
    }

    // Validate an entered PIN against the stored hash
    fun validatePin(enteredPin: String): Boolean {
        try {
            // If no PIN is stored yet, validation fails
            if (storedHashedPin == null) {
                val prefs = context.getSharedPreferences("secure_pin_prefs", Context.MODE_PRIVATE)
                storedHashedPin = prefs.getString("hashed_pin", null)
                    ?: return false
            }

            // Hash the entered PIN and compare with stored hash
            val hashedEnteredPin = hashPin(enteredPin)
            return storedHashedPin == hashedEnteredPin
        } catch (e: Exception) {
            Log.e("SecurePinValidator", "Error validating PIN", e)
            return false
        }
    }

    // Clear the stored PIN hash (e.g., for logout)
    fun clearStoredPin() {
        storedHashedPin = null
        val prefs = context.getSharedPreferences("secure_pin_prefs", Context.MODE_PRIVATE)
        prefs.edit { remove("hashed_pin") }
    }
}