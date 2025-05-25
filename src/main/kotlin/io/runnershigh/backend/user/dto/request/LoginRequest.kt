package io.runnershigh.backend.user.dto.request

data class LoginRequest(
    val loginId: String,
    val password: String,
)
