package io.runnershigh.backend.user.service

import io.runnershigh.backend.user.dto.request.LoginRequest
import io.runnershigh.backend.user.dto.request.SignupRequest
import io.runnershigh.backend.user.dto.response.LoginResponse

interface UserService {
    fun login(request: LoginRequest): LoginResponse
    fun signup(request: SignupRequest): String
    fun isLoginIdAvailable(loginId: String): Boolean
    fun isNicknameAvailable(nickname: String): Boolean
}