package com.example.myyandexapplicationv3.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myyandexapplicationv3.domain.note.model.Note
import com.example.myyandexapplicationv3.ui.note.view.AllNotesScreen
import com.example.myyandexapplicationv3.ui.note.view.NoteEditScreen
import com.example.myyandexapplicationv3.ui.note.presentation.NotesViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@Composable
fun NotesApp() {
    AppNavigation()
}

@Composable
fun AppNavigation(
    viewModel: NotesViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    NavHost(navController, startDestination = "notesList") {
        composable("notesList") {
            AllNotesScreen(
                viewModel = viewModel,
                onAddNote = { navController.navigate("editNote") },
                onEditNote = { noteId ->
                    navController.navigate("editNote/$noteId")
                }
            )
        }

        composable("editNote") {
            NoteEditScreen(
                onSave = { note ->
                    coroutineScope.launch {
                        viewModel.addNote(note)
                        navController.popBackStack()
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("editNote/{noteId}") { backStackEntry ->
            val noteId = try {
                UUID.fromString(backStackEntry.arguments?.getString("noteId") ?: "")
            } catch (e: Exception) {
                null
            }

            val noteState = if (noteId != null) {
                viewModel.getNote(noteId).collectAsState(initial = null)
            } else {
                remember { mutableStateOf<Note?>(null) }
            }

            NoteEditScreen(
                initialNote = noteState.value,
                onSave = { updatedNote ->
                    coroutineScope.launch {
                        if (noteState.value != null) {
                            viewModel.updateNote(updatedNote)
                        } else {
                            viewModel.addNote(updatedNote)
                        }
                        navController.popBackStack()
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}