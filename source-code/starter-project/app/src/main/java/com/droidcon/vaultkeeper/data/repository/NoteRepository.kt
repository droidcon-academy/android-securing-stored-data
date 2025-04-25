package com.droidcon.vaultkeeper.data.repository

import com.droidcon.vaultkeeper.data.db.NoteDao
import com.droidcon.vaultkeeper.data.model.Note
import com.droidcon.vaultkeeper.utils.CryptoUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing encrypted notes in the database
 * 
 * TODO: This class demonstrates how to securely store and retrieve notes with encryption.
 * The repository handles encryption/decryption so the UI layer only deals with plaintext.
 */
class NoteRepository(private val noteDao: NoteDao) {
    
    /**
     * Get all notes, with the content decrypted
     * 
     * TODO: Implement note decryption
     * - Retrieve all notes from the database
     * - Decrypt each note's content before returning
     * - Handle decryption exceptions gracefully
     */
    fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { notes ->
            notes.map { note ->
                try {
                    // TODO: Replace this with actual decryption using CryptoUtils
                    // Currently returning the encrypted content as-is
                    note
                } catch (e: Exception) {
                    note
                }
            }
        }
    }
    
    /**
     * Get a note by ID, with the content decrypted
     * 
     * TODO: Implement single note decryption
     * - Retrieve the note from the database
     * - Decrypt its content before returning
     * - Handle decryption exceptions gracefully
     */
    suspend fun getNoteById(id: Int): Note? {
        val note = noteDao.getNoteById(id) ?: return null
        
        return try {
            // TODO: Replace this with actual decryption using CryptoUtils
            // Currently returning the encrypted content as-is
            note
        } catch (e: Exception) {
            note
        }
    }
    
    /**
     * Insert a new note, with the content encrypted
     * 
     * TODO: Implement note encryption before storage
     * - Encrypt the note content using CryptoUtils
     * - Store both the plaintext title and encrypted content
     */
    suspend fun insertNote(title: String, content: String): Long {
        // TODO: Replace this with actual encryption using CryptoUtils
        // Currently storing the content without encryption
        val note = Note(
            title = title,
            encryptedBody = content  // Should encrypt this content
        )
        return noteDao.insertNote(note)
    }
    
    /**
     * Update a note, with the new content encrypted
     * 
     * TODO: Implement note encryption for updates
     * - Encrypt the updated content using CryptoUtils
     * - Update the database with the encrypted content
     */
    suspend fun updateNote(id: Int, title: String, content: String) {
        // TODO: Replace this with actual encryption using CryptoUtils
        // Currently storing the content without encryption
        val note = Note(
            id = id,
            title = title,
            encryptedBody = content  // Should encrypt this content
        )
        noteDao.updateNote(note)
    }
    
    // Delete a note by ID
    suspend fun deleteNote(id: Int) {
        noteDao.deleteNoteById(id)
    }
} 