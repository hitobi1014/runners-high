package io.runnershigh.backend.shared.security.jwt

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeBlank
import io.mockk.mockk
import net.datafaker.Faker
import org.springframework.security.core.userdetails.UserDetailsService
import java.util.*

class JwtTokenProviderTest : BehaviorSpec() {
    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var userDetailsService: UserDetailsService
    private val faker: Faker = Faker()

    private val secretKey = faker.lorem().characters(80, 160)
    private val userId: Int = 12
    private val nickname = "testuser"

    private fun initJwtTokenProvider(
        base64EncodedSecretKey: String =
            Base64.getEncoder().encodeToString(secretKey.toByteArray()),
        accessTokenExpire: Long = 60,
        refreshTokenExpire: Long = 60,
    ): JwtTokenProvider {
        return JwtTokenProvider(
            base64EncodedSecretKey,
            accessTokenExpire,
            refreshTokenExpire
        )
    }

    init {
        beforeSpec {
            userDetailsService = mockk(relaxed = true)
            jwtTokenProvider = initJwtTokenProvider()
        }

        Given("User ID와 닉네임이 주어진경우") {
            val token = jwtTokenProvider.generateAccessToken(userId, nickname)

            When("액세스 토큰을 생성하면") {
                Then("액세스 토큰이 생성된다.") {
                    token shouldNotBe null
                    token.shouldNotBeBlank()

                    val extractedUserId = jwtTokenProvider.getUserId(token)
                    val extractedNickname = jwtTokenProvider.getNickname(token)

                    extractedUserId shouldBe userId
                    extractedNickname shouldBe nickname
                }
            }
        }

        Given("유효한 토큰이 주어진경우") {
            val token = jwtTokenProvider.generateAccessToken(userId, nickname)

            When("토큰 유효성 검증을 하면") {
                Then("토큰이 유효하다.") {
                    jwtTokenProvider.validateToken(token).shouldBeTrue()
                }
            }
        }

        Given("만료된 토큰이 주어진 경우") {
            val expiredJWTProvider = initJwtTokenProvider(accessTokenExpire = -1)
            val expiredToken = expiredJWTProvider.generateAccessToken(userId, nickname)
            println(expiredToken)

            Thread.sleep(1000) // 확실하게 만료되기 위해 잠시 대기

            When("토큰 유효성 검증을 하면") {
                Then("예외가 발생한다.") {
                    expiredJWTProvider.validateToken(expiredToken).shouldBeFalse()
                }
            }
        }

        Given("변조된 토큰이 주어진 경우") {
            val token = jwtTokenProvider.generateAccessToken(userId, nickname)
            val tamperedToken = token.substring(0, token.length - 1) + "XX"

            When("토큰 유효성 검증을 하면") {
                Then("예외가 발생한다.") {
                    jwtTokenProvider.validateToken(tamperedToken).shouldBeFalse()
                }
            }
        }

        Given("User ID가 주어진 경우") {
            val randomUserId = faker.number().randomDigit()
            When("리프레시 토큰 생성을 하면") {
                val refreshToken = jwtTokenProvider.generateRefreshToken(randomUserId)
                Then("정상적으로 리프레시 토큰이 생성된다.") {
                    jwtTokenProvider.getUserId(refreshToken) shouldBe randomUserId
                }
            }
        }
    }

//    @Test
//    @DisplayName("인증 객체 생성")
//    fun testGetAuthentication() {
//        // given
//        val token = jwtTokenProvider.generateToken(userId, nickname)
//
//        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
//        val userDetails = User(
//            userId.toString(),
//            "", // 이미 인증 완료된 상태 가정
//            authorities
//        )
//
//        every { userDetailsService.loadUserByUsername(userId.toString()) } returns userDetails
//
//        // when
//        val authentication = jwtTokenProvider.getAuthentication(token)
//
//        // then
//        assertNotNull(authentication)
//        assertEquals(userId, authentication.name.toInt())
//        assertEquals(1, authentication.authorities.size)
//        assertTrue(authentication.authorities.containsAll(authorities))
//    }
}