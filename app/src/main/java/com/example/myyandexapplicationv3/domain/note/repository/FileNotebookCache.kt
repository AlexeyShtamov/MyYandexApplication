package com.example.myyandexapplicationv3.domain.note.repository

import com.example.myyandexapplicationv3.domain.note.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID

class FileNotebookCache(private val fileNotebook: FileNotebook) : NotebookCache {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())

    init {
        loadInitialData()
    }

    private fun loadInitialData() {

    }

    override fun getNotes(): Flow<List<Note>> = _notes

    override fun getNoteById(uid: UUID): Flow<Note?> = _notes.map { notes ->
        notes.find { it.uid == uid }
    }

    override suspend fun addNote(note: Note) {
        fileNotebook.addNote(note)
        fileNotebook.saveToFile()
        _notes.update { it + note }
    }

    override suspend fun updateNote(note: Note) {
        fileNotebook.addNote(note)
        fileNotebook.saveToFile()
        _notes.update { notes ->
            notes.map { if (it.uid == note.uid) note else it }
        }
    }

    override suspend fun deleteNote(uid: UUID) {
        fileNotebook.removeNote(uid)
        fileNotebook.saveToFile()
        _notes.update { notes ->
            notes.filterNot { it.uid == uid }
        }
    }
}