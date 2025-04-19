package com.droidcon.vaultkeeper.data.repository

import com.droidcon.vaultkeeper.data.db.NoteDao
import com.droidcon.vaultkeeper.data.model.Note
import com.droidcon.vaultkeeper.utils.CryptoUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepository(private val noteDao: NoteDao) {
    
    // Get all notes, with the content decrypted
    fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { notes ->
            notes.map { note ->
                try {
                    // Create a copy with decrypted content
                    note.copy(encryptedBody = CryptoUtils.decrypt(note.encryptedBody))
                } catch (e: Exception) {
                    // If decryption fails, just return the original note
                    note
                }
            }
        }
    }
    
    // Get a note by ID, with the content decrypted
    suspend fun getNoteById(id: Int): Note? {
        val note = noteDao.getNoteById(id) ?: return null
        
        return try {
            // Create a copy with decrypted content
            note.copy(encryptedBody = CryptoUtils.decrypt(note.encryptedBody))
        } catch (e: Exception) {
            // If decryption fails, just return the original note
            note
        }
    }
    
    // Insert a new note, with the content encrypted
    suspend fun insertNote(title: String, content: String): Long {
        val encryptedContent = CryptoUtils.encrypt(content)
        val note = Note(
            title = title,
            encryptedBody = encryptedContent
        )
        return noteDao.insertNote(note)
    }
    
    // Update a note, with the new content encrypted
    suspend fun updateNote(id: Int, title: String, content: String) {
        val encryptedContent = CryptoUtils.encrypt(content)
        val note = Note(
            id = id,
            title = title,
            encryptedBody = encryptedContent
        )
        noteDao.updateNote(note)
    }
    
    // Delete a note by ID
    suspend fun deleteNote(id: Int) {
        noteDao.deleteNoteById(id)
    }
} 