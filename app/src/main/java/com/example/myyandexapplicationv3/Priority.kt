package com.example.myapplicationyandex.model

enum class Priority {
    LOW, NORMAL, HIGH;

    companion object{
        fun from(type: String?): Priority = entries.find { it.name == type } ?: NORMAL
    }
}