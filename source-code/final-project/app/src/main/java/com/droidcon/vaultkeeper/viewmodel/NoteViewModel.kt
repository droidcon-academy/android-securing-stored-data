package com.droidcon.vaultkeeper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.droidcon.vaultkeeper.data.model.Note
import com.droidcon.vaultkeeper.data.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    
    // Expose notes as Flow
    val notes: Flow<List<Note>> = repository.getAllNotes()
    
    // Get a single note by ID
    suspend fun getNoteById(id: Int): Note? {
        return repository.getNoteById(id)
    }
    
    // Create a new note
    fun createNote(title: String, content: String) {
        viewModelScope.launch {
            repository.insertNote(title, content)
        }
    }
    
    // Update an existing note
    fun updateNote(id: Int, title: String, content: String) {
        viewModelScope.launch {
            repository.updateNote(id, title, content)
        }
    }
    
    // Delete a note
    fun deleteNote(id: Int) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }
}

// Factory for creating the ViewModel with dependencies
class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 