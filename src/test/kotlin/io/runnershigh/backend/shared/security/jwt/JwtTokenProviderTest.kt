package io.runnershigh.backend.shared.security.jwt

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import java.util.Base64

/*
* TODO 로그인 api 개발시 함께 테스트 리팩토링 & BDD 형식으로 변경
* */

//class JwtTokenProviderTest {
//    private lateinit var jwtTokenProvider: JwtTokenProvider
//    private lateinit var userDetailsService: UserDetailsService
//
//    // 테스트 값
//    private val secretKey = "testtesttest1234testtesttest".repeat(4)
//    private val base64EncodedSecretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
//    private val userId: Int = 12
//    private val nickname = "testuser"
//    private val tokenValidityInSeconds: Long = 3600
//
//    @BeforeEach
//    fun setup() {
//        userDetailsService = mockk(relaxed = true)
//
//        jwtTokenProvider = JwtTokenProvider(
//            userDetailsService,
//            base64EncodedSecretKey,
//            tokenValidityInSeconds
//        )
//    }
//
//    @Test
//    @DisplayName("토큰 생성 - 올바른 형식의 토큰이 생성되어야 함")
//    fun testGenerateToken() {
//        // when: 토큰 생성
//        val token = jwtTokenProvider.generateToken(userId, nickname)
//
//        // then: 토큰 검증
//        assertNotNull(token)
//        assertTrue(token.isNotBlank())
//
//        // 생성된 토큰 > 정보 추출
//
//        val extractedUserId = jwtTokenProvider.getUserId(token)
//        val extractedNickname = jwtTokenProvider.getNickname(token)
//
//        assertEquals(userId, extractedUserId)
//        assertEquals(nickname, extractedNickname)
//    }
//
//    @Test
//    @DisplayName("토큰 유효성 검증 - 유효한 토큰")
//    fun testValidateToken_validToken() {
//        // given
//        val token = jwtTokenProvider.generateToken(userId, nickname)
//
//        // when
//        val isValid = jwtTokenProvider.validateToken(token)
//
//        // then
//        assertTrue(isValid)
//    }
//
//    @Test
//    @DisplayName("토큰 유효성 검증 - 만료된 토큰")
//    fun testValidateToken_expiredToken() {
//        // given
//        val expiredJWTProvider = JwtTokenProvider(
//            userDetailsService,
//            base64EncodedSecretKey,
//            0L // 즉시 만료
//        )
//
//        val expiredToken = expiredJWTProvider.generateToken(userId, nickname)
//
//        Thread.sleep(1000) // 확실하게 만료되기 위해 잠시 대기
//
//        // when
//        val isValid = expiredJWTProvider.validateToken(expiredToken)
//
//        assertFalse(isValid)
//    }
//
//    @Test
//    @DisplayName("토큰 유효성 검증 - 변조된 토큰")
//    fun testValidateToken_tamperedToken() {
//        // given
//        val token = jwtTokenProvider.generateToken(userId, nickname)
//        val tamperedToken = token.substring(0, token.length - 1) + "XX"
//
//        // when
//        val isValid = jwtTokenProvider.validateToken(tamperedToken)
//
//        assertFalse(isValid)
//    }
//
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
//}