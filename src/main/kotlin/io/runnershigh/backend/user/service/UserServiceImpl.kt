package io.runnershigh.backend.user.service

import io.runnershigh.backend.shared.security.jwt.JwtTokenProvider
import io.runnershigh.backend.user.dto.request.LoginRequest
import io.runnershigh.backend.user.dto.response.LoginResponse
import io.runnershigh.backend.user.exception.UserException
import io.runnershigh.backend.user.exception.UserExceptionType
import io.runnershigh.backend.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
) : UserService {

    override fun login(loginDto: LoginRequest): LoginResponse {
        // 1. DB에서 유저 정보 조회
        val user = (userRepository.findByLoginId(loginDto.loginId)
            ?: throw UserException(UserExceptionType.USER_INVALID_CREDENTIALS))

        // 2. 패스워드 검증
        if (!passwordEncoder.matches(loginDto.password, user.password)) {
            throw UserException(UserExceptionType.USER_INVALID_CREDENTIALS)
        }

        // 3. JWT 토큰 생성
        val accessToken = jwtTokenProvider.generateAccessToken(user.id, user.nickname)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id)

        // 4. 토큰 반환
        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = user.id,
            nickname = user.nickname,
        )
    }
}