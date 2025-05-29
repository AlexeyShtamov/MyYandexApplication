package com.example.myyandexapplicationv3.domain.note.repository

import com.example.myyandexapplicationv3.domain.note.model.Note
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface NotebookCache {
    fun getNotes(): Flow<List<Note>>
    fun getNoteById(uid: UUID): Flow<Note?>
    suspend fun addNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(uid: UUID)
}