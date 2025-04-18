package com.droidcon.vaultkeeper.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val encryptedBody: String,
    val createdAt: Long = System.currentTimeMillis()
) 