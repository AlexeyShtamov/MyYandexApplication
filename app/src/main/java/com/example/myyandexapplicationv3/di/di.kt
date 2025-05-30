package com.example.myyandexapplicationv3.di

import android.content.Context
import com.example.myyandexapplicationv3.domain.note.repository.FileNotebook
import com.example.myyandexapplicationv3.domain.note.repository.FileNotebookCache
import com.example.myyandexapplicationv3.domain.note.repository.NotebookCache
import com.example.myyandexapplicationv3.domain.note.repository.NotebookRemote
import com.example.myyandexapplicationv3.domain.note.repository.NotebookRemoteImpl
import com.example.myyandexapplicationv3.domain.note.repository.NotebookRemoteStub
import com.example.myyandexapplicationv3.domain.note.repository.NotebookRepository
import com.example.myyandexapplicationv3.ui.note.presentation.NotesViewModel
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.io.File
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

val appModule = module {
    single<FileNotebook> {
        val context = get<Context>()
        val file = File(context.filesDir, "notes.json")
        if (!file.exists()) {
            file.createNewFile()
        }
        FileNotebook(file)
    }

    single<NotebookCache> {
        FileNotebookCache(get())
    }

    single<NotebookRemote> {
        NotebookRemoteImpl(authToken = "c161d7b1-0475-4d0a-bc9a-ecee7375dd42")
    }

    single<NotebookRepository> {
        NotebookRepository(
            cache = get(),
            remote = get(),
            dispatcher = Dispatchers.IO
        )
    }

    viewModel {
        NotesViewModel(get())
    }
}
