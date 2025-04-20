package com.droidcon.vaultkeeper.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.vaultkeeper.data.db.AppDatabase
import com.droidcon.vaultkeeper.data.model.Note
import com.droidcon.vaultkeeper.data.repository.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: NoteRepository
    
    val allNotes: StateFlow<List<Note>>
    
    init {
        val noteDao = AppDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        allNotes = repository.allNotes.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }
    
    fun getNoteById(id: Int, callback: (Note?) -> Unit) {
        viewModelScope.launch {
            callback(repository.getNoteById(id))
        }
    }
    
    fun insertNote(title: String, content: String, callback: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertNote(title, content)
            callback(id)
        }
    }
    
    fun updateNote(note: Note, newTitle: String, newContent: String) {
        viewModelScope.launch {
            repository.updateNote(note, newTitle, newContent)
        }
    }
    
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
} 