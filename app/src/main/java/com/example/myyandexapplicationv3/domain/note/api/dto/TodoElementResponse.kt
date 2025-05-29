package com.example.myyandexapplicationv3.domain.note.api.dto

import com.google.gson.annotations.SerializedName

data class TodoElementResponse(
    @SerializedName("status") val status: String,
    @SerializedName("element") val element: TodoItemResponse,
    @SerializedName("revision") val revision: Int
)