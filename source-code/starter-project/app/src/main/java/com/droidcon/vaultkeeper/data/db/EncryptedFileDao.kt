package com.droidcon.vaultkeeper.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.droidcon.vaultkeeper.data.model.EncryptedFile
import kotlinx.coroutines.flow.Flow

@Dao
interface EncryptedFileDao {
    @Query("SELECT * FROM encrypted_files ORDER BY createdAt DESC")
    fun getAllEncryptedFiles(): Flow<List<EncryptedFile>>
    
    @Query("SELECT * FROM encrypted_files WHERE id = :id")
    suspend fun getEncryptedFileById(id: Int): EncryptedFile?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncryptedFile(encryptedFile: EncryptedFile): Long
    
    @Query("DELETE FROM encrypted_files WHERE id = :id")
    suspend fun deleteEncryptedFileById(id: Int)
} 