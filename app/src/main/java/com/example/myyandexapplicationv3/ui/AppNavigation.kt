package com.example.myyandexapplicationv3.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.UUID

@Composable
fun NotesApp() {
    val viewModel: NotesViewModel = viewModel()

    AppNavigation(viewModel)
}

@Composable
fun AppNavigation(
    viewModel: NotesViewModel
) {
    val navController = rememberNavController()

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
                    viewModel.addNote(note)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("editNote/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            val note = try {
                viewModel.getNoteById(UUID.fromString(noteId))
            } catch (e: Exception) {
                null
            }

            NoteEditScreen(
                initialNote = note,
                onSave = { updatedNote ->
                    if (note != null) {
                        viewModel.updateNote(updatedNote)
                    } else {
                        viewModel.addNote(updatedNote)
                    }
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}