package com.example.myyandexapplicationv3.ui.note.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myyandexapplicationv3.domain.note.model.Note
import com.example.myyandexapplicationv3.domain.note.repository.NotebookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class NotesViewModel(
    private val repository: NotebookRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<NotesUiState>(NotesUiState.Loading)
    val uiState: StateFlow<NotesUiState> = _uiState

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            repository.getNotes()
                .catch { e ->
                    _uiState.value = NotesUiState.Error(e.message ?: "Unknown error")
                }
                .collect { notes ->
                    _uiState.value = NotesUiState.Success(notes)
                }
        }
    }

    suspend fun addNote(note: Note) {
        repository.addNote(note)
    }

    suspend fun updateNote(note: Note) {
        repository.updateNote(note)
    }

    suspend fun deleteNote(uid: UUID) {
        repository.deleteNote(uid)
    }

    fun getNote(uid: UUID): Flow<Note?> {
        return repository.getNoteById(uid)
    }
}

sealed class NotesUiState {
    object Loading : NotesUiState()
    data class Error(val message: String) : NotesUiState()
    data class Success(val notes: List<Note>) : NotesUiState()
}