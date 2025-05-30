package com.example.myyandexapplicationv3.domain.note.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.myyandexapplicationv3.domain.note.api.dto.TodoElementResponse
import com.example.myyandexapplicationv3.domain.note.api.dto.TodoItemResponse
import com.example.myyandexapplicationv3.domain.note.api.dto.TodoListResponse
import com.example.myyandexapplicationv3.domain.note.api.exception.ApiError
import com.example.myyandexapplicationv3.domain.note.model.Note
import com.example.myyandexapplicationv3.domain.note.model.Priority
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import timber.log.Timber
import java.util.UUID

class NotebookRemoteImpl(
    private val baseUrl: String = "https://hive.mrdekk.ru/todo",
    private val authToken: String
) : NotebookRemote {

    private val client = OkHttpClient()
    private val jsonMediaType = "application/json".toMediaType()
    private var currentRevision: Int = 0


    override suspend fun fetchNotes(): List<Note> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/list")
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch notes: ${response.code}")
        }

        val jsonResponse = response.body?.string() ?: throw Exception("Empty response")
        val todoResponse = parseTodoListResponse(jsonResponse)

        currentRevision = todoResponse.revision

        todoResponse.list.map { convertToNote(it) }
    }

    override suspend fun fetchNote(uid: UUID): Note? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/list/$uid")
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.code == 404) return@withContext null
            if (!response.isSuccessful) {
                throw Exception("Failed to fetch note: ${response.code}")
            }

            val jsonResponse = response.body?.string() ?: throw Exception("Empty response")
            val todoResponse = parseTodoElementResponse(jsonResponse)

            convertToNote(todoResponse.element)
        } catch (e: Exception) {
            Timber.e(e, "Error fetching note $uid")
            throw e
        }
    }

    override suspend fun addNote(note: Note): Unit = withContext(Dispatchers.IO) {
        val todoItem = convertToTodoItem(note)
        val json = gson.toJson(todoItem)

        val request = Request.Builder()
            .url("$baseUrl/list")
            .post(json.toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $authToken")
            .addHeader("X-Last-Known-Revision", currentRevision.toString())
            .build()

        executeRequest(request, "add")
    }

    override suspend fun updateNote(note: Note): Unit = withContext(Dispatchers.IO) {
        val todoItem = convertToTodoItem(note)
        val json = gson.toJson(todoItem)

        val request = Request.Builder()
            .url("$baseUrl/list/${note.uid}")
            .put(json.toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $authToken")
            .addHeader("X-Last-Known-Revision", currentRevision.toString())
            .build()

        executeRequest(request, "update")
    }

    override suspend fun deleteNote(uid: UUID): Unit = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/list/$uid")
            .delete()
            .addHeader("Authorization", "Bearer $authToken")
            .addHeader("X-Last-Known-Revision", currentRevision.toString())
            .build()

        executeRequest(request, "delete")
    }

    private fun executeRequest(request: Request, operation: String): Boolean {
        return try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw Exception("Failed to $operation note: ${response.code}")
            }
            true
        } catch (e: Exception) {
            Timber.e(e, "Error $operation note")
            throw e
        }
    }

    private val gson = GsonBuilder().create()

    private fun parseTodoListResponse(json: String): TodoListResponse {
        return try {
            gson.fromJson(json, TodoListResponse::class.java).also {
                if (it.status != "ok") {
                    throw IllegalStateException("Server returned status: ${it.status}")
                }
            }
        } catch (e: JsonSyntaxException) {
            Timber.e(e, "Failed to parse TodoListResponse")
            throw ApiError.BadRequest("Invalid server response format")
        } catch (e: IllegalStateException) {
            Timber.e(e, "Server returned error status")
            throw ApiError.ServerError(e.message ?: "Server error")
        }
    }

    private fun parseTodoElementResponse(json: String): TodoElementResponse {
        return try {
            gson.fromJson(json, TodoElementResponse::class.java).also {
                if (it.status != "ok") {
                    throw IllegalStateException("Server returned status: ${it.status}")
                }
            }
        } catch (e: JsonSyntaxException) {
            Timber.e(e, "Failed to parse TodoElementResponse")
            throw ApiError.BadRequest("Invalid server response format")
        } catch (e: IllegalStateException) {
            Timber.e(e, "Server returned error status")
            throw ApiError.ServerError(e.message ?: "Server error")
        }
    }

    private fun convertToNote(todoItem: TodoItemResponse): Note {
        return Note(
            uid = todoItem.id,
            title = todoItem.text,
            content = "",
            color = todoItem.color?.let { Color(android.graphics.Color.parseColor(it)).toArgb() } ?: 0,
            priority = when (todoItem.importance) {
                "low" -> Priority.LOW
                "important" -> Priority.HIGH
                else -> Priority.NORMAL
            },
        )
    }

    private fun convertToTodoItem(note: Note): TodoItemResponse {
        return TodoItemResponse(
            id = note.uid,
            text = note.title,
            importance = when (note.priority) {
                Priority.LOW -> "low"
                Priority.HIGH -> "important"
                else -> "basic"
            },
            deadline = null,
            done = false,
            color = note.color?.let { String.format("#%06X", 0xFFFFFF and it) },
            createdAt = System.currentTimeMillis(),
            changedAt = System.currentTimeMillis(),
            lastUpdatedBy = "android_device"
        )
    }
}