package io.runnershigh.backend.user.controller

import io.runnershigh.backend.shared.response.ApiResponse
import io.runnershigh.backend.shared.response.ResponseUtils
import io.runnershigh.backend.user.dto.request.LoginRequest
import io.runnershigh.backend.user.dto.response.LoginResponse
import io.runnershigh.backend.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService,
) {
    @PostMapping("/login")
    fun userLogin(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        val response = userService.login(request)
        return ResponseUtils.success(response, "로그인 성공")
    }
}