package com.droidcon.vaultkeeper.data.repository

import android.content.Context
import android.net.Uri
import com.droidcon.vaultkeeper.data.db.EncryptedFileDao
import com.droidcon.vaultkeeper.data.model.EncryptedFile
import com.droidcon.vaultkeeper.utils.FileUtils
import kotlinx.coroutines.flow.Flow
import java.io.File

class EncryptedFileRepository(
    private val context: Context,
    private val encryptedFileDao: EncryptedFileDao
) {
    
    // Get all encrypted files
    fun getAllEncryptedFiles(): Flow<List<EncryptedFile>> {
        return encryptedFileDao.getAllEncryptedFiles()
    }
    
    // Import and encrypt a file
    suspend fun importAndEncryptFile(sourceUri: Uri, fileName: String): Long {
        // Encrypt the file and store it in the app's private storage
        val filePath = FileUtils.encryptTextFile(context, sourceUri, fileName)
        
        // Get the size of the encrypted file
        val fileSize = File(filePath).length()
        
        // Create an EncryptedFile object to track in the database
        val encryptedFile = EncryptedFile(
            fileName = fileName,
            filePath = filePath,
            fileSize = fileSize
        )
        
        // Insert into the database and return the ID
        return encryptedFileDao.insertEncryptedFile(encryptedFile)
    }
    
    // Decrypt a file and return its contents
    suspend fun decryptFile(id: Int): String? {
        val encryptedFile = encryptedFileDao.getEncryptedFileById(id) ?: return null
        return FileUtils.decryptTextFile(context, encryptedFile.fileName)
    }
    
    // Delete an encrypted file
    suspend fun deleteEncryptedFile(id: Int): Boolean {
        val encryptedFile = encryptedFileDao.getEncryptedFileById(id) ?: return false
        
        // Delete the actual file from storage
        val fileDeleted = FileUtils.deleteEncryptedFile(context, encryptedFile.fileName)
        
        // Delete the database entry
        if (fileDeleted) {
            encryptedFileDao.deleteEncryptedFileById(id)
        }
        
        return fileDeleted
    }
} 