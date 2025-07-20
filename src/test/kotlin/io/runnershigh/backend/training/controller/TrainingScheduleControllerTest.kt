package io.runnershigh.backend.training.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.runnershigh.backend.fixture.training.TrainingInfoFixture
import io.runnershigh.backend.fixture.training.TrainingScheduleFixture
import io.runnershigh.backend.shared.response.CommonResponseMessage
import io.runnershigh.backend.training.service.TrainingSchedulesServiceImpl
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(
    controllers = [TrainingScheduleController::class],
    excludeAutoConfiguration = [DataSourceAutoConfiguration::class, JpaRepositoriesAutoConfiguration::class]
)
@ExtendWith(MockKExtension::class)
@AutoConfigureMockMvc(addFilters = false)
class TrainingScheduleControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @MockkBean private val trainingScheduleUseCase: TrainingSchedulesServiceImpl,
    @Autowired private val objectMapper: ObjectMapper,
) : BehaviorSpec({

    val baseUrl = "/api/training-schedule"

    Given("다음 훈련 일정이 있는 상태에서") {
        val mockSchedule = TrainingInfoFixture.createNextTrainingSchedule()

        every { trainingScheduleUseCase.getNextUpcomingTrainingSchedule() } returns mockSchedule

        When("다음 훈련 일정 조회 API를 호출 하면") {
            Then("성공적으로 훈련 일정이 반환된다") {
                mockMvc.perform(
                    get("$baseUrl/next")
                )
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value(CommonResponseMessage.SUCCESS_GET_DATA.message))
                    .andExpect(jsonPath("$.data.scheduleId").value(mockSchedule.scheduleId))
                    .andExpect(jsonPath("$.data.title").value(mockSchedule.title))

                verify(exactly = 1) { trainingScheduleUseCase.getNextUpcomingTrainingSchedule() }
            }
        }
    }

    Given("이번 주 훈련 일정이 있는 상태에서") {
        val mockSchedule1 =
            TrainingScheduleFixture.createMockWeeklyTrainingSchedule(scheduleId = 1L)
        val mockSchedule2 =
            TrainingScheduleFixture.createMockWeeklyTrainingSchedule(scheduleId = 2L)
        val mockSchedule3 =
            TrainingScheduleFixture.createMockWeeklyTrainingSchedule(scheduleId = 3L)
        val mockList = listOf(mockSchedule1, mockSchedule2, mockSchedule3)

        every { trainingScheduleUseCase.getThisWeekTrainingSchedules() } returns mockList

        When("이번 주 훈련 일정을 조회하면") {
            Then("성공적으로 훈련 일정 3개가 반환된다.") {
                mockMvc.perform(
                    get("$baseUrl/current-week")
                )
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value(CommonResponseMessage.SUCCESS_GET_DATA.message))
                    .andExpect(jsonPath("$.data.length()").value(3))
                    .andExpect(jsonPath("$.data[0].scheduleId").value(mockSchedule1.scheduleId))
                    .andExpect(jsonPath("$.data[0].title").value(mockSchedule1.title))
                    .andExpect(jsonPath("$.data[1].scheduleId").value(mockSchedule2.scheduleId))
                    .andExpect(jsonPath("$.data[1].title").value(mockSchedule2.title))

                verify(exactly = 1) { trainingScheduleUseCase.getThisWeekTrainingSchedules() }
            }
        }
    }

    Given("훈련 일정 저장 요청이 들어왔을 때") {
        val saveDto = TrainingInfoFixture.createSaveTrainingInfo(
            title = "템포런 훈련",
            location = "올림픽 공원",
            scheduledDateTime = LocalDateTime.now().plusDays(3)
        )
        val mockTrainingSchedule = TrainingInfoFixture.createTrainingSchedule()

        every { trainingScheduleUseCase.createTrainingSchedule(any()) } returns mockTrainingSchedule

        When("유효한 훈련 일정 데이터로 저장 API를 호출하면") {
            Then("성공적으로 훈련 일정이 저장된다") {
                mockMvc.perform(
                    post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveDto))
                )
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value(CommonResponseMessage.SUCCESS_CREATE_DATA.message))

                verify(exactly = 1) { trainingScheduleUseCase.createTrainingSchedule(any()) }
            }
        }
    }

    Given("유효하지 않은 훈련 일정 저장 요청이 들어왔을 때") {
        val invalidDto = mapOf(
            "title" to "",  // 제목이 비어있음
            "scheduledDateTime" to "2024-01-01T00:00:00",  // 과거 날짜
            "color" to "MINT",
            "groups" to emptyList<Any>()  // 그룹이 비어있음
        )

        When("유효하지 않은 데이터로 저장 API를 호출하면") {
            Then("400 Bad Request 에러가 발생한다") {
                mockMvc.perform(
                    post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                )
                    .andExpect(status().isBadRequest)

                verify(exactly = 0) { trainingScheduleUseCase.createTrainingSchedule(any()) }
            }
        }
    }

    Given("이번 주 훈련 요약 정보를 조회할 때") {
        val mockSummary = TrainingScheduleFixture.createMockSummaryThisWeekSchedule()

        every { trainingScheduleUseCase.getSummaryThisWeekForSchedule() } returns mockSummary

        When("주간 훈련 요약 API를 호출하면") {
            Then("성공적으로 요약 정보가 반환된다") {
                mockMvc.perform(
                    get("$baseUrl/current-week/summary")
                )
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value(CommonResponseMessage.SUCCESS_GET_DATA.message))
                    .andExpect(jsonPath("$.data.scheduleCount").value(mockSummary.scheduleCount))
                    .andExpect(jsonPath("$.data.totalDistance").value(mockSummary.totalDistance))
                    .andExpect(jsonPath("$.data.totalTime").value(mockSummary.totalTime.toString()))

                verify(exactly = 1) { trainingScheduleUseCase.getSummaryThisWeekForSchedule() }
            }
        }
    }

})