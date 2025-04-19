package com.droidcon.vaultkeeper.utils

import android.content.Context
import android.net.Uri
import android.util.Base64
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FileUtils {
    companion object {
        // Extension for encrypted files
        private const val ENCRYPTED_EXTENSION = ".encrypted"
        
        // Filename for storing IVs
        private const val IV_FILENAME_SUFFIX = ".iv"
        
        /**
         * Encrypts a text file and stores it in the app's private storage
         * Returns the path to the encrypted file
         */
        fun encryptTextFile(context: Context, sourceUri: Uri, fileName: String): String {
            val outputFile = createEncryptedFile(context, fileName)
            
            // Open input stream from the source URI
            context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                FileOutputStream(outputFile).use { fileOutputStream ->
                    // Set up encryption
                    val (cipherOutputStream, iv) = CryptoUtils.encryptStream(fileOutputStream)
                    
                    // Save the IV to a separate file for later decryption
                    saveIv(context, fileName, iv)
                    
                    // Write encrypted data
                    cipherOutputStream.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
            
            return outputFile.absolutePath
        }
        
        /**
         * Decrypts a previously encrypted file and returns its contents as a string
         */
        fun decryptTextFile(context: Context, fileName: String): String {
            val encryptedFile = getEncryptedFile(context, fileName)
            val iv = loadIv(context, fileName)
            
            return FileInputStream(encryptedFile).use { fileInputStream ->
                val cipherInputStream = CryptoUtils.decryptStream(fileInputStream, iv)
                cipherInputStream.use { inputStream ->
                    inputStream.readBytes().toString(Charsets.UTF_8)
                }
            }
        }
        
        /**
         * Deletes an encrypted file and its IV file
         */
        fun deleteEncryptedFile(context: Context, fileName: String): Boolean {
            val encryptedFile = getEncryptedFile(context, fileName)
            val ivFile = getIvFile(context, fileName)
            
            val fileDeleted = encryptedFile.delete()
            val ivDeleted = ivFile.delete()
            
            return fileDeleted && ivDeleted
        }
        
        /**
         * Creates a file for encrypted data in the app's private storage
         */
        private fun createEncryptedFile(context: Context, fileName: String): File {
            val filesDir = context.filesDir
            val encryptedFileName = "${fileName}$ENCRYPTED_EXTENSION"
            return File(filesDir, encryptedFileName)
        }
        
        /**
         * Gets reference to an encrypted file
         */
        private fun getEncryptedFile(context: Context, fileName: String): File {
            val filesDir = context.filesDir
            val encryptedFileName = "${fileName}$ENCRYPTED_EXTENSION"
            return File(filesDir, encryptedFileName)
        }
        
        /**
         * Gets reference to an IV file
         */
        private fun getIvFile(context: Context, fileName: String): File {
            val filesDir = context.filesDir
            val ivFileName = "${fileName}$IV_FILENAME_SUFFIX"
            return File(filesDir, ivFileName)
        }
        
        /**
         * Saves the initialization vector (IV) to a file
         */
        private fun saveIv(context: Context, fileName: String, iv: ByteArray) {
            val ivFile = getIvFile(context, fileName)
            val ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT)
            ivFile.writeText(ivBase64)
        }
        
        /**
         * Loads the initialization vector (IV) from a file
         */
        private fun loadIv(context: Context, fileName: String): ByteArray {
            val ivFile = getIvFile(context, fileName)
            val ivBase64 = ivFile.readText()
            return Base64.decode(ivBase64, Base64.DEFAULT)
        }
    }
} 