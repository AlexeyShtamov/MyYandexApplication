package com.example.myyandexapplicationv3.domain.note.repository

import com.example.myyandexapplicationv3.domain.note.model.Note
import java.util.UUID

interface NotebookRemote {
    suspend fun fetchNotes(): List<Note>
    suspend fun fetchNote(uid: UUID): Note?
    suspend fun addNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(uid: UUID)
}