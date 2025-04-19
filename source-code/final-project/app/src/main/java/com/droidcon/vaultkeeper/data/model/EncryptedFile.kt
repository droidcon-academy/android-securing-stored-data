package com.droidcon.vaultkeeper.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "encrypted_files")
data class EncryptedFile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val filePath: String,
    val fileSize: Long,
    val createdAt: Long = System.currentTimeMillis()
) 