package com.example.myyandexapplicationv3.domain.note.api.exception

import okhttp3.Response

sealed class ApiError : Exception() {
    data class Unauthorized(override val message: String) : ApiError()
    data class NotFound(override val message: String) : ApiError()
    data class BadRequest(override val message: String) : ApiError()
    data class ServerError(override val message: String) : ApiError()
    data class UnknownError(override val message: String) : ApiError()
}
