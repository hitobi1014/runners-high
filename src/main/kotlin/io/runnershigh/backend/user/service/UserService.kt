package io.runnershigh.backend.user.service

import io.runnershigh.backend.user.dto.request.LoginRequest
import io.runnershigh.backend.user.dto.response.LoginResponse

interface UserService {
    fun login(request: LoginRequest): LoginResponse
}