package com.example.myyandexapplicationv3.di

import androidx.room.Room
import com.example.myyandexapplicationv3.domain.note.repository.remote.NotebookRemote
import com.example.myyandexapplicationv3.domain.note.repository.remote.NotebookRemoteImpl
import com.example.myyandexapplicationv3.domain.note.repository.NotebookRepository
import com.example.myyandexapplicationv3.domain.note.repository.NotebookRepositoryImpl
import com.example.myyandexapplicationv3.domain.note.repository.local.AppDatabase
import com.example.myyandexapplicationv3.ui.note.presentation.NotesViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "notes-database"
        ).build()
    }

    single { get<AppDatabase>().noteDao() }

    single<NotebookRemote> {
        NotebookRemoteImpl(authToken = "your_token_here")
    }

    single<NotebookRepository> {
        NotebookRepositoryImpl(
            remote = get(),
            noteDao = get(),
            dispatcher = Dispatchers.IO
        )
    }

    viewModel { NotesViewModel(get()) }
}