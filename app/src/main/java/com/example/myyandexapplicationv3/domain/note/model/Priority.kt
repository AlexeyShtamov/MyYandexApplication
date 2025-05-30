package com.example.myyandexapplicationv3.domain.note.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.myyandexapplicationv3.R


enum class Priority {
    LOW, NORMAL, HIGH;

    companion object{
        fun from(type: String?): Priority = entries.find { it.name == type } ?: NORMAL
    }
}

@Composable
fun Priority.toUiString(): String {
    return when (this) {
        Priority.LOW -> stringResource(R.string.low_priority)
        Priority.NORMAL -> stringResource(R.string.medium_priority)
        Priority.HIGH -> stringResource(R.string.high_priority)
    }
}

fun Priority.toColor(): Color {
    return when (this) {
        Priority.HIGH -> Color.Red
        Priority.LOW, Priority.NORMAL -> Color.Black
    }
}