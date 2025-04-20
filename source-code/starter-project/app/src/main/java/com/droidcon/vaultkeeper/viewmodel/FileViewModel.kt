package com.droidcon.vaultkeeper.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.droidcon.vaultkeeper.data.model.EncryptedFile
import com.droidcon.vaultkeeper.data.repository.EncryptedFileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FileViewModel(private val repository: EncryptedFileRepository) : ViewModel() {
    
    // Expose files as Flow
    val files: Flow<List<EncryptedFile>> = repository.getAllEncryptedFiles()
    
    // Import and encrypt a file - implementation will be part of the codelab exercise
    fun importFile(sourceUri: Uri, fileName: String) {
        viewModelScope.launch {
            // TODO: Implement file encryption as part of the codelab
            repository.importAndEncryptFile(sourceUri, fileName)
        }
    }
    
    // Get decrypted file contents - implementation will be part of the codelab exercise
    suspend fun getFileContents(id: Int): String? {
        // TODO: Implement file decryption as part of the codelab
        return repository.decryptFile(id)
    }
    
    // Delete a file
    fun deleteFile(id: Int) {
        viewModelScope.launch {
            repository.deleteEncryptedFile(id)
        }
    }
}

// Factory for creating the ViewModel with dependencies
class FileViewModelFactory(private val repository: EncryptedFileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
