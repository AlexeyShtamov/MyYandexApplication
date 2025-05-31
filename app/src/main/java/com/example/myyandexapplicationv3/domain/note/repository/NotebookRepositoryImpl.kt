package com.example.myyandexapplicationv3.domain.note.repository

import com.example.myyandexapplicationv3.domain.note.repository.local.dao.NoteDao
import com.example.myyandexapplicationv3.domain.note.repository.local.enitity.NoteEntity
import com.example.myyandexapplicationv3.domain.note.model.Note
import com.example.myyandexapplicationv3.domain.note.model.Priority
import com.example.myyandexapplicationv3.domain.note.repository.remote.NotebookRemote
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID

class NotebookRepositoryImpl(
    private val remote: NotebookRemote,
    private val noteDao: NoteDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NotebookRepository {

    override fun getNotes(): Flow<List<Note>> = noteDao.getAll()
        .map { entities -> entities.map { it.toNote() } }
        .flowOn(dispatcher)

    override fun getNoteById(uid: UUID): Flow<Note?> = noteDao.getById(uid)
        .map { it?.toNote() }
        .flowOn(dispatcher)

    override suspend fun addNote(note: Note) = withContext(dispatcher) {
        noteDao.insert(note.toEntity(isSynced = false))

        trySyncOperation(
            operation = { remote.addNote(note) },
            onSuccess = { revision ->
                noteDao.updateSyncStatus(note.uid, true)
                noteDao.updateRevision(note.uid, revision)
            }
        )
    }

    override suspend fun updateNote(note: Note) = withContext(dispatcher) {
        noteDao.update(note.toEntity(isSynced = false))

        trySyncOperation(
            operation = { remote.updateNote(note) },
            onSuccess = { revision ->
                noteDao.updateSyncStatus(note.uid, true)
                noteDao.updateRevision(note.uid, revision)
            }
        )
    }

    override suspend fun deleteNote(uid: UUID) {
        val note = noteDao.getById(uid).first()?.toNote()

        note?.let { noteDao.delete(it.toEntity(isSynced = false)) }

        note?.let {
            trySyncOperation(
                operation = { remote.deleteNote(uid) },
                onSuccess = {}
            )
        }
    }

    override suspend fun syncAll() = withContext(dispatcher) {
        try {
            val remoteNotes = remote.fetchNotes()

            noteDao.deleteAllSynced()
            remoteNotes.forEach { note ->
                noteDao.insert(note.toEntity(isSynced = true))
            }

            val unsyncedNotes = noteDao.getAllUnsynced().first()
            unsyncedNotes.forEach { entity ->
                try {
                    val note = entity.toNote()
                    when {
                        entity.isDeleted -> remote.deleteNote(note.uid)
                        else -> remote.addNote(note)
                    }
                    noteDao.updateSyncStatus(note.uid, true)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync note ${entity.uid}")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to complete full sync")
            throw e
        }
    }

    private suspend fun <T> trySyncOperation(
        operation: suspend () -> T,
        onSuccess: suspend (T) -> Unit
    ) {
        try {
            val result = operation()
            onSuccess(result)
        } catch (e: Exception) {
            Timber.e(e, "Sync operation failed")
            throw e
        }
    }

    private fun Note.toEntity(isSynced: Boolean): NoteEntity {
        return NoteEntity(
            uid = this.uid,
            title = this.title,
            content = this.content,
            color = this.color,
            priority = when (this.priority) {
                Priority.LOW -> "low"
                Priority.HIGH -> "important"
                Priority.NORMAL -> "basic"
            },
            isSynced = isSynced,
            isDeleted = false,
            revision = null
        )
    }

    private fun NoteEntity.toNote(): Note {
        return Note(
            uid = this.uid,
            title = this.title,
            content = this.content,
            color = this.color,
            priority = when (this.priority) {
                "low" -> Priority.LOW
                "important" -> Priority.HIGH
                else -> Priority.NORMAL
            }
        )
    }
}