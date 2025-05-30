package com.example.myyandexapplicationv3.domain.note.repository

import com.example.myyandexapplicationv3.domain.note.model.Note
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID

class NotebookRepository(
    private val cache: NotebookCache,
    private val remote: NotebookRemote,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun getNotes(): Flow<List<Note>> = cache.getNotes()
        .onEach { notes ->
            if (notes.isEmpty()) {
                refreshFromRemote()
            }
        }

    fun getNoteById(uid: UUID): Flow<Note?> = cache.getNoteById(uid)
        .onEach { note ->
            if (note == null) {
                refreshNoteFromRemote(uid)
            }
        }

    suspend fun addNote(note: Note) {
        withContext(dispatcher) {
            cache.addNote(note)
            remote.addNote(note)
        }
    }

    suspend fun updateNote(note: Note) {
        withContext(dispatcher) {
            cache.updateNote(note)
            remote.updateNote(note)
        }
    }

    suspend fun deleteNote(uid: UUID) {
        withContext(dispatcher) {
            cache.deleteNote(uid)
            remote.deleteNote(uid)
        }
    }

    private suspend fun refreshFromRemote() {
        try {
            val remoteNotes = remote.fetchNotes()
            remoteNotes.forEach { cache.addNote(it) }
        } catch (e: Exception) {
            Timber.e(e, "Failed to refresh notes from remote")
        }
    }

    private suspend fun refreshNoteFromRemote(uid: UUID) {
        try {
            remote.fetchNote(uid)?.let { cache.addNote(it) }
        } catch (e: Exception) {
            Timber.e(e, "Failed to refresh note $uid from remote")
        }
    }
}