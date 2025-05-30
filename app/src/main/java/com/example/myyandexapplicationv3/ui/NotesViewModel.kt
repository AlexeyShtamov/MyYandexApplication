package com.example.myyandexapplicationv3.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.myapplicationyandex.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class NotesViewModel : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    fun addNote(note: Note) {
        _notes.update { currentNotes ->
            currentNotes + note
        }
    }

    fun deleteNote(uid: UUID) {
        _notes.update { currentNotes ->
            currentNotes.filterNot { it.uid == uid }
        }
    }

    fun updateNote(updatedNote: Note) {
        _notes.update { currentNotes ->
            currentNotes.map { note ->
                if (note.uid == updatedNote.uid) updatedNote else note
            }
        }
    }

    fun getNoteById(uid: UUID): Note? {
        return _notes.value.find { it.uid == uid }
    }
}