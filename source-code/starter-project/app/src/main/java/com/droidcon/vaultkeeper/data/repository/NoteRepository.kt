package com.droidcon.vaultkeeper.data.repository

import com.droidcon.vaultkeeper.data.db.NoteDao
import com.droidcon.vaultkeeper.data.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()
    
    suspend fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)
    }
    
    suspend fun insertNote(title: String, content: String): Long {
        // TODO: In the final implementation, this is where we'll encrypt the content
        // For now, we'll just store it as plaintext with a placeholder note
        
        val encryptedContent = "This will be encrypted in the final implementation: $content"
        
        val note = Note(
            title = title,
            encryptedBody = encryptedContent
        )
        
        return noteDao.insertNote(note)
    }
    
    suspend fun updateNote(note: Note, newTitle: String, newContent: String): Note {
        // TODO: In the final implementation, we'll encrypt the new content here
        
        val updatedNote = note.copy(
            title = newTitle,
            encryptedBody = "This will be encrypted in the final implementation: $newContent"
        )
        
        noteDao.updateNote(updatedNote)
        return updatedNote
    }
    
    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }
} 