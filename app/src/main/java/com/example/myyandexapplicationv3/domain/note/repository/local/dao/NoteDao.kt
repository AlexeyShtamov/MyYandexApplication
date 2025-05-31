package com.example.myyandexapplicationv3.domain.note.repository.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myyandexapplicationv3.domain.note.repository.local.enitity.NoteEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0")
    fun getAll(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE uid = :uid AND isDeleted = 0")
    fun getById(uid: UUID): Flow<NoteEntity?>

    @Query("SELECT * FROM notes WHERE isSynced = 0")
    fun getAllUnsynced(): Flow<List<NoteEntity>>

    @Query("DELETE FROM notes WHERE isSynced = 1")
    suspend fun deleteAllSynced()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity)

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("UPDATE notes SET isSynced = :isSynced WHERE uid = :uid")
    suspend fun updateSyncStatus(uid: UUID, isSynced: Boolean)

    @Query("UPDATE notes SET revision = :revision WHERE uid = :uid")
    suspend fun updateRevision(uid: UUID, revision: Int)
}