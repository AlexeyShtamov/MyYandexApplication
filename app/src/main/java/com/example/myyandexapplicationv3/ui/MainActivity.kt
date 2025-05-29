package com.example.myyandexapplicationv3.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myyandexapplicationv3.di.appModule
import com.example.myyandexapplicationv3.navigation.NotesApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesApp()

        }

    }
}







