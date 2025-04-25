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
 * TODO: Implement secure preferences storage using DataStore with manual encryption.
 * This class will:
 * 1. Store all preference values encrypted using AES-256-GCM
 * 2. Use the Android Keystore to securely manage the encryption key
 * 3. Handle key generation, encryption, and decryption automatically
 * 4. Provide both blocking synchronous API (for compatibility) and Flow-based reactive API
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
     * 
     * TODO: Implement key verification in the Android Keystore
     */
    private fun isKeyReady(): Boolean {
        // TODO: Check if the encryption key exists in the Android Keystore
        return false
    }

    /**
     * Generates a new AES-256 encryption key in the Android Keystore.
     * This key is protected by the Android Keystore and cannot be exported.
     * 
     * TODO: Implement key generation using Android Keystore
     * - Create a KeyGenerator for AES
     * - Configure it with appropriate KeyGenParameterSpec
     * - Generate and store the key in the AndroidKeyStore
     */
    private fun generateEncryptionKey() {
        // TODO: Implement key generation
        Log.d(TAG, "Key generation not implemented yet")
    }

    /**
     * Encrypts a string value using AES-256-GCM with a key from the Android Keystore.
     * The initialization vector (IV) is prepended to the encrypted data so it can be
     * retrieved during decryption.
     *
     * TODO: Implement string encryption
     * - Retrieve the key from the Android Keystore
     * - Initialize a cipher for encryption
     * - Encrypt the data and save the IV
     * - Return the encrypted data in a format that includes the IV
     * 
     * @param plainText The string to encrypt
     * @return Base64-encoded string containing the IV and encrypted data
     */
    private fun encrypt(plainText: String): String {
        // TODO: Implement encryption
        // This is a placeholder implementation that just returns the plaintext
        // You will need to replace this with actual encryption
        return plainText
    }
    
    /**
     * Decrypts a string value that was encrypted using the encrypt method.
     * Extracts the IV from the beginning of the data, then uses it with
     * the key from Android Keystore to decrypt the data.
     *
     * TODO: Implement string decryption
     * - Retrieve the key from the Android Keystore
     * - Extract the IV from the encrypted data
     * - Initialize a cipher for decryption with the IV
     * - Decrypt and return the plaintext
     * 
     * @param encryptedText Base64-encoded string containing the IV and encrypted data
     * @return The decrypted string
     */
    private fun decrypt(encryptedText: String): String {
        // TODO: Implement decryption
        // This is a placeholder implementation that just returns the input
        // You will need to replace this with actual decryption
        return encryptedText
    }

    /**
     * Saves a string securely in DataStore with encryption
     *
     * TODO: Implement secure string storage
     * - Encrypt the value using the encrypt method
     * - Store it in DataStore using the provided key
     * 
     * @param key The preference key
     * @param value The string value to encrypt and store
     */
    private suspend fun saveString(key: String, value: String) {
        // TODO: Implement secure string storage
        val prefKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = value  // Currently storing unencrypted, you'll need to encrypt this
        }
    }
    
    /**
     * Retrieves a string securely from DataStore with decryption
     *
     * TODO: Implement secure string retrieval
     * - Get the encrypted value from DataStore
     * - Decrypt it using the decrypt method
     * - Return the plaintext or the default value if not found
     * 
     * @param key The preference key
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The decrypted string or the default value
     */
    private suspend fun getString(key: String, defaultValue: String? = null): String? {
        // TODO: Implement secure string retrieval
        val prefKey = stringPreferencesKey(key)
        val value = context.dataStore.data.first()[prefKey]
        return value ?: defaultValue  // Currently returning unencrypted, you'll need to decrypt this
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
