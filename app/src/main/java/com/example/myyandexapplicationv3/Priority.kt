package com.example.myapplicationyandex.model

import androidx.compose.ui.graphics.Color


enum class Priority {
    LOW, NORMAL, HIGH;

    companion object{
        fun from(type: String?): Priority = entries.find { it.name == type } ?: NORMAL
    }
}

fun Priority.toUiString(): String {
    return when (this) {
        Priority.LOW -> "\uD83D\uDE34 Низкий"
        Priority.NORMAL -> "\uD83D\uDE4F Средний"
        Priority.HIGH -> "❗ Высокий"
    }
}

fun Priority.toColor(): Color {
    return when (this) {
        Priority.HIGH -> Color.Red
        Priority.LOW, Priority.NORMAL -> Color.Black
    }
}