package com.example.myyandexapplicationv3.domain.note.model

import android.graphics.Color
import org.json.JSONObject
import timber.log.Timber
import java.util.UUID

fun Note.parse(json: JSONObject): Note? {
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

val Note.json: JSONObject
    get() {
        val json = JSONObject()
        json.put("uid", uid.toString())
        json.put("title", title)
        json.put("content", content)

        if (color != Color.WHITE) {
            json.put("color", color)
        }

        if (priority != Priority.NORMAL) {
            json.put("priority", priority.name)
        }

        return json
    }

