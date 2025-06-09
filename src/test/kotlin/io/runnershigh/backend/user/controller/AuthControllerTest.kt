package io.runnershigh.backend.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.runnershigh.backend.user.dto.request.SignupRequest
import io.runnershigh.backend.user.entity.enum.AgeGroup
import io.runnershigh.backend.user.entity.enum.Gender
import io.runnershigh.backend.user.service.UserService
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(
    controllers = [AuthController::class],
    excludeAutoConfiguration = [DataSourceAutoConfiguration::class, JpaRepositoriesAutoConfiguration::class]
)
@ExtendWith(MockKExtension::class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @MockkBean private val userService: UserService,
) : BehaviorSpec({

    val baseUrl = "/api/auth"

    given("이미 등록된 로그인ID가 있을 때") {
        val loginId = "test1234"

        `when`("로그인ID 사용가능 여부를 검사하면") {
            every { userService.isLoginIdAvailable(loginId) } returns false

            then("사용이 불가하면 false를 반환한다.") {
                mockMvc.perform(
                    get("$baseUrl/check-login-id")
                        .param("loginId", loginId)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").value(false))
                    .andExpect(jsonPath("$.message").value("로그인 ID 중복 확인 완료"))
            }
        }
    }

    given("이미 등록된 닉네임이 있을 때") {
        val nickname = "테스트닉네임1234"

        `when`("닉네임 사용가능 여부를 검사하면") {
            every { userService.isNicknameAvailable(nickname) } returns false

            then("사용이 불가하면 false를 반환한다.") {
                mockMvc.perform(
                    get("$baseUrl/check-nickname")
                        .param("nickname", nickname)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").value(false))
                    .andExpect(jsonPath("$.message").value("닉네임 중복 확인 완료"))
            }
        }
    }

    given("올바른 회원정보를 입력하고") {
        val signupRequest = SignupRequest(
            loginId = "test1234",
            password = "test12341234",
            nickname = "테스트닉네임",
            gender = Gender.MALE,
            ageGroup = AgeGroup.TWENTIES
        )
        `when`("회원 가입을 하면") {
            every { userService.signup(any<SignupRequest>()) } returns signupRequest.loginId

            then("성공적으로 회원가입이 완료된다.") {
                mockMvc.perform(
                    MockMvcRequestBuilders.post("$baseUrl/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest))
                )
                    .andExpect(status().is2xxSuccessful)
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").value(signupRequest.loginId))
            }
        }
    }

})
