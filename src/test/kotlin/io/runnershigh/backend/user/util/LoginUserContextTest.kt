package io.runnershigh.backend.user.util

import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.runnershigh.backend.user.domain.enum.AgeGroup
import io.runnershigh.backend.user.domain.enum.Gender
import io.runnershigh.backend.user.infrastructure.entity.UserEntity
import io.runnershigh.backend.user.infrastructure.repository.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

class LoginUserContextTest {
    private lateinit var loginUserContext: LoginUserContext
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setup() {
        userRepository = mockk()
        loginUserContext = LoginUserContext(userRepository)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        SecurityContextHolder.clearContext()
    }

    @Test
    @DisplayName("정상적으로 유저 반환")
    fun getCurrentContextUserId() {
        // given
        val securityContext: SecurityContext = mockk()
        val authentication = UsernamePasswordAuthenticationToken("123", null)
        every { securityContext.authentication } returns authentication
        SecurityContextHolder.setContext(securityContext)

        val mockUser = createUser()
        every { userRepository.findById(123) } returns Optional.of(mockUser)

        // when
        val user = loginUserContext.getCurrentUser()

        // then
        assertNotNull(user)
        assertEquals(123, user.id)
        assertEquals("한강", user.nickname)
        verify { userRepository.findById(123) }
    }

    @Test
    @DisplayName("유저를 찾을 수 없을때 예외 발생")
    fun getCurrentUser_IllegalStateException() {
        val securityContext: SecurityContext = mockk()
        val authentication = UsernamePasswordAuthenticationToken("123", null)
        every { securityContext.authentication } returns authentication
        SecurityContextHolder.setContext(securityContext)

        every { userRepository.findById(123) } returns Optional.empty()

        val exception =
            assertThrows(IllegalStateException::class.java) { loginUserContext.getCurrentUser() }

        assertEquals("User not found", exception.message)
        verify { userRepository.findById(123) }
    }

    private fun createUser(): UserEntity {
        return UserEntity(
            id = 123,
            loginId = "testId",
            password = "test1234",
            nickname = "한강",
            gender = Gender.MALE,
            profileImage = "",
            ageGroup = AgeGroup.TEENS
        )
    }
}