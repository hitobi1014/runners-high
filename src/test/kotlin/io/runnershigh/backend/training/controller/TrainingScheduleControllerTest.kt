package io.runnershigh.backend.training.controller

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.runnershigh.backend.shared.response.CommonResponseMessage
import io.runnershigh.backend.training.service.TrainingSchedulesServiceImpl
import io.runnershigh.backend.training.entity.enum.TrainingStatus
import io.runnershigh.backend.training.dto.response.ReadTrainingSchedule
import io.runnershigh.backend.training.dto.response.NextTrainingSchedule
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.time.LocalTime

@WebMvcTest(
    controllers = [TrainingScheduleController::class],
    excludeAutoConfiguration = [DataSourceAutoConfiguration::class, JpaRepositoriesAutoConfiguration::class]
)
@ExtendWith(MockKExtension::class)
@AutoConfigureMockMvc(addFilters = false)
class TrainingScheduleControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @MockkBean private val trainingScheduleUseCase: TrainingSchedulesServiceImpl,
) : BehaviorSpec({

    val baseUrl = "/api/training-schedule"

    Given("다음 훈련 일정이 있는 상태에서") {
        val mockSchedule = createNextTrainingSchedule()

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
        val mockSchedule1 = createTrainingSchedule()
        val mockSchedule2 = createTrainingSchedule(
            id = 2L,
            title = "보라매 공원 템포런",
            scheduledDate = LocalDate.now().plusDays(1)
        )
        val mockSchedule3 = createTrainingSchedule(
            id = 3L,
            title = "남산 업힐",
            scheduledDate = LocalDate.now().plusDays(3)
        )
        val mockList = listOf(mockSchedule1, mockSchedule2, mockSchedule3)

        every { trainingScheduleUseCase.getCurrentWeekTrainingSchedules() } returns mockList

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
                    .andExpect(jsonPath("$.data[0].id").value(mockSchedule1.id))
                    .andExpect(jsonPath("$.data[0].title").value(mockSchedule1.title))
                    .andExpect(jsonPath("$.data[1].id").value(mockSchedule2.id))
                    .andExpect(jsonPath("$.data[1].title").value(mockSchedule2.title))

                verify(exactly = 1) { trainingScheduleUseCase.getCurrentWeekTrainingSchedules() }
            }
        }
    }

})

private fun createTrainingSchedule(
    id: Long = 1L,
    title: String = "올림픽 공원 펀런",
    location: String = "올림픽 공원",
    scheduledDate: LocalDate = LocalDate.now(),
    description: String = "즐겁게 뛰기",
    status: TrainingStatus = TrainingStatus.PLANNED,
    color: String = "#ff0000",
) = ReadTrainingSchedule(
    id = id,
    title = title,
    location = location,
    scheduledDate = scheduledDate,
    description = description,
    status = status,
    color = color
)

private fun createNextTrainingSchedule(
    scheduleId: Long = 1L,
    title: String = "올림픽 공원 펀런",
    scheduledDate: LocalDate = LocalDate.now(),
    totalDistance: Double = 10.0,
    totalTime: LocalTime = LocalTime.of(0, 50)
) = NextTrainingSchedule(
    scheduleId = scheduleId,
    title = title,
    scheduledDate = scheduledDate,
    totalDistance = totalDistance,
    totalTime = totalTime
)