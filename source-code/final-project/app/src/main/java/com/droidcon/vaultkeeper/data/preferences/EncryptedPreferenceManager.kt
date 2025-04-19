@file:Suppress("DEPRECATION")

package com.droidcon.vaultkeeper.data.preferences

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedPreferenceManager(context: Context) {

    companion object {
        private const val ENCRYPTED_PREFS_FILE_NAME = "vault_keeper_secure_prefs"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    }

    private val masterKeyAlias: MasterKey
    private val sharedPreferences: SharedPreferences

    init {
        // Create or retrieve the Master Key for encryption/decryption
        masterKeyAlias = createMasterKey(context)

        // Create or retrieve the encrypted SharedPreferences
        sharedPreferences = createEncryptedSharedPreferences(context, masterKeyAlias)
    }

    private fun createMasterKey(context: Context): MasterKey {
        // Create a KeyGenParameterSpec with enhanced security features
        val spec = KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        return MasterKey.Builder(context)
            .setKeyGenParameterSpec(spec)
            .build()
    }

    private fun createEncryptedSharedPreferences(
        context: Context,
        masterKey: MasterKey
    ): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }
}
