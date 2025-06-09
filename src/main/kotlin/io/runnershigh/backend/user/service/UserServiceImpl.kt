package io.runnershigh.backend.user.service

import io.runnershigh.backend.shared.security.jwt.JwtTokenProvider
import io.runnershigh.backend.user.dto.request.LoginRequest
import io.runnershigh.backend.user.dto.request.SignupRequest
import io.runnershigh.backend.user.dto.response.LoginResponse
import io.runnershigh.backend.user.entity.UserEntity
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

    private val logger = mu.KotlinLogging.logger {}

    override fun login(request: LoginRequest): LoginResponse {
        // 1. DB에서 유저 정보 조회
        val user = (userRepository.findByLoginId(request.loginId)
//            ?: throw UserException(UserExceptionType.USER_INVALID_CREDENTIALS))
            ?: run {
                logger.warn { "유저가 존재하지 않음: ${request.loginId} not found" }
                throw UserException(UserExceptionType.USER_INVALID_CREDENTIALS)
            })

        // 2. 패스워드 검증
        if (!passwordEncoder.matches(request.password, user.password)) {
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

    override fun signup(request: SignupRequest): String {
        // 1. 로그인ID 중복체크
        if (!isLoginIdAvailable(request.loginId)) {
            throw UserException(UserExceptionType.USER_LOGIN_ID_ALREADY_EXISTS)
        }

        // 2. 닉네임 중복체크
        if (!isNicknameAvailable(request.nickname)) {
            throw UserException(UserExceptionType.USER_NICKNAME_ALREADY_EXISTS)
        }

        // 3. 회원 가입 처리
        val user = UserEntity(
            loginId = request.loginId,
            password = passwordEncoder.encode(request.password),
            nickname = request.nickname,
            gender = request.gender,
            ageGroup = request.ageGroup
        )
        val savedUser = userRepository.save(user)

        return savedUser.loginId
    }

    override fun isLoginIdAvailable(loginId: String): Boolean {
        return !userRepository.existsByLoginId(loginId)
    }

    override fun isNicknameAvailable(nickname: String): Boolean {
        return !userRepository.existsByNickname(nickname)
    }
}