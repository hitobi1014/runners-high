package io.runnershigh.backend.user.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.runnershigh.backend.fixture.UserFixture
import io.runnershigh.backend.shared.security.jwt.JwtTokenProvider
import io.runnershigh.backend.training.service.TrainingPlanItemsServiceImpl
import io.runnershigh.backend.user.dto.request.LoginRequest
import io.runnershigh.backend.user.entity.UserEntity
import io.runnershigh.backend.user.exception.UserException
import io.runnershigh.backend.user.exception.UserExceptionType
import io.runnershigh.backend.user.repository.UserRepository
import net.datafaker.Faker
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Locale

@ExtendWith(MockKExtension::class)
class UserServiceImplTest : BehaviorSpec() {
    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    @MockK
    private lateinit var jwtTokenProvider: JwtTokenProvider

    private lateinit var userService: UserServiceImpl

    private val faker = Faker(Locale.KOREA)

    init {
        beforeSpec {
            userService = UserServiceImpl(userRepository, passwordEncoder, jwtTokenProvider)
        }
        afterTest {
            clearMocks(userRepository, passwordEncoder, jwtTokenProvider)
        }

        given("로그인 요청이 들어왔을 때") {
            val password = faker.internet().password(10, 20)
            val loginId = "test1"
            val request = LoginRequest(
                loginId = loginId,
                password = password
            )
            `when`("올바른 로그인 정보로 로그인하면") {
                val user = UserFixture.createDefault(loginId = loginId, password = password)
                val expectedToken = faker.internet().uuid()
                val expectedRefreshToken = faker.internet().uuid()

                every { userRepository.findByLoginId(loginId) } returns user
                every { passwordEncoder.matches(password, user.password) } returns true
                every {
                    jwtTokenProvider.generateAccessToken(
                        user.id,
                        user.nickname
                    )
                } returns expectedToken
                every { jwtTokenProvider.generateRefreshToken(user.id) } returns expectedRefreshToken

                then("로그인 정보를 담은 response를 반환한다.") {
                    val result = userService.login(request)

                    result shouldNotBe null
                    result.accessToken shouldBe expectedToken
                    result.refreshToken shouldBe expectedRefreshToken
                    result.userId shouldBe user.id
                    result.nickname shouldBe user.nickname

                    verify(exactly = 1) { userRepository.findByLoginId(loginId) }
                    verify(exactly = 1) { passwordEncoder.matches(password, user.password) }
                    verify(exactly = 1) {
                        jwtTokenProvider.generateAccessToken(
                            user.id,
                            user.nickname
                        )
                    }
                    verify(exactly = 1) { jwtTokenProvider.generateRefreshToken(user.id) }
                }
            }

            `when`("잘못된 패스워드로 로그인하면") {
                val user = UserFixture.createDefault(loginId = loginId, password = password)

                every { userRepository.findByLoginId(loginId) } returns user
                every { passwordEncoder.matches(password, user.password) } returns false

                then("예외가 발생한다.") {
                    shouldThrow<UserException> {
                        userService.login(request)
                    }.run {
                        exceptionType shouldBe UserExceptionType.USER_INVALID_CREDENTIALS
                    }

                    verify(exactly = 1) { userRepository.findByLoginId(loginId) }
                    verify(exactly = 1) { passwordEncoder.matches(password, user.password) }
                    verify(exactly = 0) { jwtTokenProvider.generateAccessToken(any(), any()) }
                    verify(exactly = 0) { jwtTokenProvider.generateRefreshToken(any()) }
                }
            }
        }
    }


}
