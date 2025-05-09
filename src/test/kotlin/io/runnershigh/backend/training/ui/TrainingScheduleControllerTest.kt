package io.runnershigh.backend.training.ui

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.runnershigh.backend.shared.response.CommonResponseMessage
import io.runnershigh.backend.training.application.TrainingSchedulesUseCase
import io.runnershigh.backend.training.domain.enum.TrainingStatus
import io.runnershigh.backend.training.ui.dto.response.ReadTrainingSchedule
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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

@WebMvcTest(
    controllers = [TrainingScheduleController::class],
    excludeAutoConfiguration = [DataSourceAutoConfiguration::class, JpaRepositoriesAutoConfiguration::class]
)
@ExtendWith(MockKExtension::class)
@AutoConfigureMockMvc(addFilters = false)
class TrainingScheduleControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var trainingScheduleUseCase: TrainingSchedulesUseCase

    @Test
    @DisplayName("다음 훈련 일정 조회")
    fun getNextSchedule() {
        //given
        val mockSchedule = ReadTrainingSchedule(
            id = 1L,
            title = "올림픽 공원 펀런",
            location = "올림픽 공원",
            scheduledDate = LocalDate.now(),
            description = "즐겁게 뛰기",
            status = TrainingStatus.PLANNED,
            color = "#ff0000"
        )

        every { trainingScheduleUseCase.getNextUpcomingTrainingSchedule() } returns mockSchedule

        //when
        val response = mockMvc.perform(
            get("/api/training-schedule/next")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value(CommonResponseMessage.SUCCESS_GET_DATA.message))
            .andExpect(jsonPath("$.data.id").value(mockSchedule.id))
            .andExpect(jsonPath("$.data.title").value(mockSchedule.title))
            .andReturn()

        //then
        verify { trainingScheduleUseCase.getNextUpcomingTrainingSchedule() }
    }

    @Test
    fun `이번 주에 등록된 훈련 일정 목록을 조회한다`() {
        //given

        //when

        //then
    }
}