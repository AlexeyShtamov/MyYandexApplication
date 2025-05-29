package com.example.myyandexapplicationv3.domain.note.api.dto

import com.google.gson.annotations.SerializedName

data class TodoListResponse(
    @SerializedName("status") val status: String,
    @SerializedName("list") val list: List<TodoItemResponse>,
    @SerializedName("revision") val revision: Int
)