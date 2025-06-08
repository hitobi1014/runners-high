package io.runnershigh.backend.user.controller

import io.runnershigh.backend.shared.response.ApiResponse
import io.runnershigh.backend.shared.response.ResponseUtils
import io.runnershigh.backend.user.dto.request.LoginRequest
import io.runnershigh.backend.user.dto.request.SignupRequest
import io.runnershigh.backend.user.dto.response.LoginResponse
import io.runnershigh.backend.user.service.UserService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Validated
class AuthController(
    private val userService: UserService,
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/login")
    fun userLogin(@RequestBody @Valid request: LoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        val response = userService.login(request)
        logger.info { "로그인 성공, 유저 ID: ${response.userId}" }
        return ResponseUtils.success(response, "로그인 성공")
    }

    @PostMapping("/signup")
    fun userSignup(@RequestBody @Valid request: SignupRequest): ResponseEntity<ApiResponse<String>> {
        val response = userService.signup(request)
        logger.info { "회원가입 성공, 유저 ID: $response" }
        return ResponseUtils.created(response, "회원가입 성공")
    }

    @GetMapping("/check-login-id")
    fun checkLoginIdAvailable(
        @RequestParam
        @NotBlank(message = "로그인ID는 필수입니다.")
        @Size(min = 6, max = 30, message = "로그인ID는 6자 이상 30자 이하로 입력해야 합니다.")
        loginId: String,
    ): ResponseEntity<ApiResponse<Boolean>> {
        val isAvailable = userService.isLoginIdAvailable(loginId)
        return ResponseUtils.success(isAvailable, "로그인 ID 중복 확인 완료")
    }

    @GetMapping("/check-nickname")
    fun checkNicknameAvailable(
        @RequestParam
        @Size(min = 1, max = 20, message = "닉네임은 1자 이상 20자 이하로 입력해야 합니다.")
        nickname: String,
    ): ResponseEntity<ApiResponse<Boolean>> {
        val isAvailable = userService.isNicknameAvailable(nickname)
        return ResponseUtils.success(isAvailable, "닉네임 중복 확인 완료")
    }
}