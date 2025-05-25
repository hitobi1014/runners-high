package io.runnershigh.backend.user.dto.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: Int,
    val nickname: String,
)
