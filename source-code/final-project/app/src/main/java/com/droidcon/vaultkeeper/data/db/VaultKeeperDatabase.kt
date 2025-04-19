package com.droidcon.vaultkeeper.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.droidcon.vaultkeeper.data.model.EncryptedFile
import com.droidcon.vaultkeeper.data.model.Note

@Database(entities = [Note::class, EncryptedFile::class], version = 1, exportSchema = false)
abstract class VaultKeeperDatabase : RoomDatabase() {
    
    abstract fun noteDao(): NoteDao
    abstract fun encryptedFileDao(): EncryptedFileDao
    
    companion object {
        @Volatile
        private var INSTANCE: VaultKeeperDatabase? = null
        
        fun getDatabase(context: Context): VaultKeeperDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VaultKeeperDatabase::class.java,
                    "vault_keeper_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 