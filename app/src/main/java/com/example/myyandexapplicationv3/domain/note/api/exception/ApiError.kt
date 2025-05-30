package com.example.myyandexapplicationv3.domain.note.api.exception

import okhttp3.Response

sealed class ApiError : Exception() {
    data class Unauthorized(override val message: String) : ApiError()
    data class NotFound(override val message: String) : ApiError()
    data class BadRequest(override val message: String) : ApiError()
    data class ServerError(override val message: String) : ApiError()
    data class UnknownError(override val message: String) : ApiError()
}

private fun handleError(response: Response): Nothing {
    when (response.code) {
        401 -> throw ApiError.Unauthorized("Authorization failed")
        404 -> throw ApiError.NotFound("Resource not found")
        400 -> throw ApiError.BadRequest("Bad request: ${response.body?.string()}")
        500 -> throw ApiError.ServerError("Server error")
        else -> throw ApiError.UnknownError("Unknown error: ${response.code}")
    }
}