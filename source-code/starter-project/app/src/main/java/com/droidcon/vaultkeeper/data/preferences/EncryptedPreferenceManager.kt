package com.droidcon.vaultkeeper.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Placeholder class for EncryptedPreferenceManager
 * 
 * In the final implementation, this class will:
 * - Use EncryptedSharedPreferences to securely store sensitive data
 * - Implement Master Key generation for encryption
 * - Configure secure key specs with KeyGenParameterSpec
 */
class EncryptedPreferenceManager(context: Context) {

    companion object {
        private const val ENCRYPTED_PREFS_FILE_NAME = "vault_keeper_secure_prefs"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    }

    // In the final implementation, this would be EncryptedSharedPreferences
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        ENCRYPTED_PREFS_FILE_NAME, Context.MODE_PRIVATE
    )

    /**
     * Get biometric authentication setting
     */
    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }
    
    /**
     * Set biometric authentication setting
     */
    fun setBiometricEnabled(enabled: Boolean) =
        sharedPreferences.edit { putBoolean(KEY_BIOMETRIC_ENABLED, enabled) }
} 