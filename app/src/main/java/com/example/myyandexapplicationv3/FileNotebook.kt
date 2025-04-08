package com.example.myapplicationyandex.model

import org.json.JSONArray
import timber.log.Timber
import java.io.File
import java.util.UUID

class FileNotebook(private val file: File) {

    private val notes: MutableMap<String, Note> = mutableMapOf()

    init {
        loadFromFile()
    }

    fun addNote(note: Note) {
        notes[note.uid.toString()] = note
        Timber.i("Note with uuid %s is added", note.uid)
    }

    fun removeNote(uid: UUID) {
        notes.remove(uid.toString())
        Timber.i("Note with uuid %s is removed", uid.toString())
    }

    fun saveToFile() {
        val jsonArray = JSONArray()
        notes.forEach { jsonArray.put(it.value.json) }

        file.bufferedWriter().use { out -> out.write(jsonArray.toString()) }
        Timber.i("Saved to file")

    }

    private fun loadFromFile() {
        file.bufferedReader().use { inn ->
            val jsonArray = JSONArray(inn.toString())
            for (i in 0..<jsonArray.length()){
                Note.parse(jsonArray.getJSONObject(i))?.let { notes.put(it.uid.toString(), it) }
            }
        }
        Timber.i("Loaded from file")
    }
}