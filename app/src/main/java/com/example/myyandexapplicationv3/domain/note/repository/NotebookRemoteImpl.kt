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
    private val baseUrl: String = "https://beta.mrdekk.ru/todo",
    private val authToken: String
) : NotebookRemote {

    private val client = OkHttpClient()
    private val jsonMediaType = "application/json".toMediaType()

    override suspend fun fetchNotes(): List<Note> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/list")
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw Exception("Failed to fetch notes: ${response.code}")
            }

            val jsonResponse = response.body?.string() ?: throw Exception("Empty response")
            val todoResponse = parseTodoListResponse(jsonResponse)

            todoResponse.list.map { todoItem ->
                convertToNote(todoItem)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching notes")
            throw e
        }
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

    override suspend fun uploadNote(note: Note): Unit = withContext(Dispatchers.IO) {
        val todoItem = convertToTodoItem(note)
        val json = JSONObject().apply {
            put("id", todoItem.id)
            put("text", todoItem.text)
            put("importance", todoItem.importance)
            todoItem.deadline?.let { put("deadline", it) }
            put("done", todoItem.done)
            todoItem.color?.let { put("color", it) }
            put("created_at", todoItem.createdAt)
            put("changed_at", todoItem.changedAt)
            put("last_updated_by", todoItem.lastUpdatedBy)
        }.toString()

        val request = Request.Builder()
            .url("$baseUrl/list/${note.uid}")
            .put(json.toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        executeRequest(request, "upload")
    }

    override suspend fun deleteNote(uid: UUID): Unit = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/list/$uid")
            .delete()
            .addHeader("Authorization", "Bearer $authToken")
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
            content = "", // Если нужно сохранить content отдельно
            color = todoItem.color?.let { Color(android.graphics.Color.parseColor(it)).toArgb() } ?: 0,
            priority = when (todoItem.importance) {
                "low" -> Priority.LOW
                "important" -> Priority.HIGH
                else -> Priority.NORMAL
            },
            // Другие поля по необходимости
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
            deadline = null, // Можно добавить deadline из note
            done = false,
            color = note.color?.let { String.format("#%06X", 0xFFFFFF and it) },
            createdAt = System.currentTimeMillis(),
            changedAt = System.currentTimeMillis(),
            lastUpdatedBy = "android_device" // Можно использовать уникальный ID устройства
        )
    }
}