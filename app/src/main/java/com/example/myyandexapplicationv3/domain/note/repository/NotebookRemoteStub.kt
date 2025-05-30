package com.example.myyandexapplicationv3.domain.note.repository

import com.example.myyandexapplicationv3.domain.note.model.Note
import timber.log.Timber
import java.util.UUID

class NotebookRemoteStub : NotebookRemote {
    private val remoteNotes = mutableMapOf<UUID, Note>()

    override suspend fun fetchNotes(): List<Note> {
        Timber.d("Fetching notes from remote")
        return remoteNotes.values.toList()
    }

    override suspend fun fetchNote(uid: UUID): Note? {
        Timber.d("Fetching note $uid from remote")
        return remoteNotes[uid]
    }

    override suspend fun uploadNote(note: Note) {
        Timber.d("Uploading note ${note.uid} to remote")
        remoteNotes[note.uid] = note
    }

    override suspend fun deleteNote(uid: UUID) {
        Timber.d("Deleting note $uid from remote")
        remoteNotes.remove(uid)
    }
}