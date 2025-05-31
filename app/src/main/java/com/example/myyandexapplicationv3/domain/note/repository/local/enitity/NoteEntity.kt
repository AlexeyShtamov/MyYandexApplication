package com.example.myyandexapplicationv3.domain.note.repository.local.enitity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val uid: UUID,
    val title: String,
    val content: String,
    val color: Int,
    val priority: String,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val revision: Int? = null
)