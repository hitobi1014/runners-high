package io.runnershigh.backend.training.application.command

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.runnershigh.backend.fixture.UserFixture
import io.runnershigh.backend.training.domain.mapper.toEntity
import io.runnershigh.backend.training.exception.TrainingException
import io.runnershigh.backend.training.exception.TrainingExceptionType
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.training.infrastructure.repository.command.TrainingSchedulesCommandRepository
import io.runnershigh.backend.training.ui.dto.SaveTrainingSchedule
import io.runnershigh.backend.user.util.LoginUserContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class TrainingSchedulesCommandUseCaseTest {

    @MockK
    private lateinit var repository: TrainingSchedulesCommandRepository

    @MockK
    private lateinit var loginUserContext: LoginUserContext

    private lateinit var useCase: TrainingSchedulesCommandUseCase

    @BeforeEach
    fun setup() {
        useCase = TrainingSchedulesCommandUseCase(repository, loginUserContext)
    }

    @Test
    @DisplayName("훈련일정 정상 저장")
    fun createTrainingSchedule() {
        //given
        val dto = saveTrainingSchedule()
        val user = userEntity()

        every { loginUserContext.getCurrentUser() } returns user
        every { repository.save(ofType<TrainingSchedules>()) } returns dto.toEntity(user)

        //when
        useCase.createTrainingSchedule(dto)

        //then
        verify(exactly = 1) {
            loginUserContext.getCurrentUser()
            repository.save(match { schedule ->
                schedule.title == dto.title &&
                        schedule.location == dto.location &&
                        schedule.scheduledDate == dto.scheduleDate &&
                        schedule.description == dto.description
                schedule.user == user
            })
        }
    }

    @Test
    @DisplayName("과거 날짜로 훈련일정 등록시 예외 발생")
    fun createTrainingSchedule_withPastDate_throwsException() {
        //given
        val pastDate = LocalDate.now().minusDays(1)
        val dto = saveTrainingSchedule().copy(scheduleDate = pastDate, title = "회복")
        val user = userEntity()

        every { loginUserContext.getCurrentUser() } returns user

        //when
        //then
        val exception = assertThrows<TrainingException> {
            useCase.createTrainingSchedule(dto)
        }

        assertEquals(TrainingExceptionType.CANNOT_REGISTER_PAST_TRAINING, exception.exceptionType)

        verify(exactly = 0) {
            repository.save(any())
        }
    }

    private fun userEntity() = UserFixture.createDefault(id = 1)

    private fun saveTrainingSchedule() = SaveTrainingSchedule(
        title = "템포런",
        location = "보라매공원",
        scheduleDate = LocalDate.of(2025, 4, 5),
        description = "빡세게 달려볼까"
    )


}
