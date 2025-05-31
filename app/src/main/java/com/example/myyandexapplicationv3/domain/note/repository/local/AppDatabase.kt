package com.example.myyandexapplicationv3.domain.note.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myyandexapplicationv3.domain.note.repository.local.dao.NoteDao
import com.example.myyandexapplicationv3.domain.note.repository.local.enitity.NoteEntity

@Database(entities = [NoteEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}