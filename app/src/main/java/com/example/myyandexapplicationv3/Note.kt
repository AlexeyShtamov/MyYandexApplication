package com.example.myapplicationyandex.model

import android.graphics.Color
import android.util.Log
import org.json.JSONObject
import timber.log.Timber
import java.util.UUID

data class Note(
    val uid: UUID = UUID.randomUUID(),
    val title: String,
    val content: String,
    val color: Int = Color.WHITE,
    val priority: Priority = Priority.NORMAL,
    ) {

    companion object {
        fun parse(json: JSONObject): Note? {
            return try {
                Note(
                    uid = UUID.fromString(json.getString("uid")),
                    title = json.getString("title"),
                    content = json.getString("content"),
                    color = json.optInt("color", Color.WHITE),
                    priority = Priority.from(json.getString("priority"))
                )
            } catch (e: Exception) {
                Timber.e("Note", "Note parse failed", e)
                return null
            }
        }

    }
}