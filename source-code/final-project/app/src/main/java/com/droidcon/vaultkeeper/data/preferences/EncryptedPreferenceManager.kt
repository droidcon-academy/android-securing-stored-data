package com.droidcon.vaultkeeper.data.preferences

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Extension property for the DataStore
 * Each instance of the dataStore will refer to the same underlying storage
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vault_keeper_secure_prefs")

/**
 * A secure preferences manager that uses DataStore with manual encryption via the Android Keystore.
 * This class replaces the deprecated EncryptedSharedPreferences approach with a more modern and
 * secure implementation that uses direct Android Keystore API.
 *
 * This approach:
 * 1. Stores all preference values encrypted using AES-256-GCM
 * 2. Uses the Android Keystore to securely manage the encryption key
 * 3. Handles key generation, encryption, and decryption automatically
 * 4. Provides both blocking synchronous API (for compatibility) and Flow-based reactive API
 */
class EncryptedPreferenceManager(private val context: Context) {

    companion object {
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEYSTORE_ALIAS = "VaultKeeperPrefsKey"
        private const val TAG = "EncryptedPreferenceManager"
    }

    init {
        // Ensure encryption key is available on initialization
        if (!isKeyReady()) {
            generateEncryptionKey()
        }
    }

    /**
     * Checks if our encryption key exists in the Android Keystore
     */
    private fun isKeyReady(): Boolean {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.containsAlias(KEYSTORE_ALIAS)
    }

    /**
     * Generates a new AES-256 encryption key in the Android Keystore.
     * This key is protected by the Android Keystore and cannot be exported.
     */
    private fun generateEncryptionKey() {
        try {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
            
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
            Log.d(TAG, "Encryption key generated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error generating encryption key", e)
            throw e
        }
    }

    /**
     * Encrypts a string value using AES-256-GCM with a key from the Android Keystore.
     * The initialization vector (IV) is prepended to the encrypted data so it can be
     * retrieved during decryption.
     *
     * @param plainText The string to encrypt
     * @return Base64-encoded string containing the IV and encrypted data
     */
    private fun encrypt(plainText: String): String {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val key = keyStore.getKey(KEYSTORE_ALIAS, null) as SecretKey
            
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, key)
            
            // Save IV for decryption - GCM mode requires a unique IV for each encryption
            val iv = cipher.iv
            
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charset.forName("UTF-8")))
            
            // Combine IV and encrypted data into a single byte array
            val combined = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
            
            // Convert to Base64 for string storage
            return Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e(TAG, "Encryption error", e)
            throw e
        }
    }
    
    /**
     * Decrypts a string value that was encrypted using the encrypt method.
     * Extracts the IV from the beginning of the data, then uses it with
     * the key from Android Keystore to decrypt the data.
     *
     * @param encryptedText Base64-encoded string containing the IV and encrypted data
     * @return The decrypted string
     */
    private fun decrypt(encryptedText: String): String {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val key = keyStore.getKey(KEYSTORE_ALIAS, null) as SecretKey
            
            // Decode from Base64
            val combined = Base64.decode(encryptedText, Base64.DEFAULT)
            
            // Extract IV from the beginning of the data (GCM IV is typically 12 bytes)
            val iv = combined.copyOfRange(0, 12)
            val encryptedBytes = combined.copyOfRange(12, combined.size)
            
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes, Charset.forName("UTF-8"))
        } catch (e: Exception) {
            Log.e(TAG, "Decryption error", e)
            throw e
        }
    }

    /**
     * Saves a string securely in DataStore with encryption
     *
     * @param key The preference key
     * @param value The string value to encrypt and store
     */
    private suspend fun saveString(key: String, value: String) {
        val encryptedValue = encrypt(value)
        val prefKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = encryptedValue
        }
    }
    
    /**
     * Retrieves a string securely from DataStore with decryption
     *
     * @param key The preference key
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The decrypted string or the default value
     */
    private suspend fun getString(key: String, defaultValue: String? = null): String? {
        val prefKey = stringPreferencesKey(key)
        val encryptedValue = context.dataStore.data.first()[prefKey]
        return if (encryptedValue != null) {
            decrypt(encryptedValue)
        } else {
            defaultValue
        }
    }

    /**
     * Checks if biometric authentication is enabled
     * This is a synchronous blocking version for API compatibility
     *
     * @return true if biometric authentication is enabled, false otherwise
     */
    fun isBiometricEnabled(): Boolean {
        return runBlocking {
            val result = getString(KEY_BIOMETRIC_ENABLED, "false")
            result?.toBoolean() == true
        }
    }
    
    /**
     * Sets whether biometric authentication is enabled
     * This is a synchronous blocking version for API compatibility
     *
     * @param enabled true to enable biometric authentication, false to disable
     */
    fun setBiometricEnabled(enabled: Boolean) {
        runBlocking {
            saveString(KEY_BIOMETRIC_ENABLED, enabled.toString())
        }
    }
}
