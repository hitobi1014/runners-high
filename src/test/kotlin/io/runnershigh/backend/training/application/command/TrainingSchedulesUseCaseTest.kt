package io.runnershigh.backend.training.application.command

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.runnershigh.backend.fixture.TrainingScheduleFixture
import io.runnershigh.backend.fixture.UserFixture
import io.runnershigh.backend.shared.util.DateUtils
import io.runnershigh.backend.training.application.TrainingSchedulesUseCase
import io.runnershigh.backend.training.domain.enum.TrainingStatus
import io.runnershigh.backend.training.domain.mapper.toDto
import io.runnershigh.backend.training.domain.mapper.toEntity
import io.runnershigh.backend.training.exception.TrainingException
import io.runnershigh.backend.training.exception.TrainingExceptionType
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.training.infrastructure.repository.TrainingSchedulesRepository
import io.runnershigh.backend.training.infrastructure.repository.querydsl.TrainingScheduleQuerydsl
import io.runnershigh.backend.training.ui.dto.request.SaveTrainingSchedule
import io.runnershigh.backend.user.util.LoginUserContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
class TrainingSchedulesUseCaseTest {

    @MockK
    private lateinit var repository: TrainingSchedulesRepository

    @MockK
    private lateinit var trainingScheduleQuerydsl: TrainingScheduleQuerydsl

    @MockK
    private lateinit var loginUserContext: LoginUserContext

    private lateinit var useCase: TrainingSchedulesUseCase

    @BeforeEach
    fun setup() {
        useCase =
            TrainingSchedulesUseCase(repository, trainingScheduleQuerydsl, loginUserContext)
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
                        schedule.scheduledDate == dto.scheduledDate &&
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
        val dto = saveTrainingSchedule().copy(scheduledDate = pastDate, title = "회복")
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

    @Test
    @DisplayName("당일 훈련일정 등록 테스트")
    fun createTrainingSchedule_withCurrentDate() {
        //given
        val currentDate = LocalDate.now()
        val dto = saveTrainingSchedule().copy(scheduledDate = currentDate)
        val user = userEntity()

        every { loginUserContext.getCurrentUser() } returns user
        every { repository.save(ofType<TrainingSchedules>()) } returns dto.toEntity(user)

        //when
        useCase.createTrainingSchedule(dto)

        //then
        verify(exactly = 1) {
            repository.save(any())
        }
    }

    @Test
    @DisplayName("훈련일정 날짜가 1년이후로 등록시 오류발생")
    fun createTrainingSchedule_withFutureDate_throwsException() {
        //given
        val futureDate = LocalDate.now().plusDays(366)
        val dto = saveTrainingSchedule().copy(scheduledDate = futureDate, title = "마라톤")
        val user = userEntity()

        every { loginUserContext.getCurrentUser() } returns user

        //when
        //then
        val exception = assertThrows<TrainingException> {
            useCase.createTrainingSchedule(dto)
        }

        assertEquals(
            TrainingExceptionType.CANNOT_REGISTER_TRAINING_BEYOND_ONE_YEAR,
            exception.exceptionType
        )

        verify(exactly = 0) {
            repository.save(any())
        }
    }

    @Test
    @DisplayName("유저가 등록한 훈련일정 전체 목록 가져오기")
    fun getTrainingSchedules() {
        //given
        val mockUser = userEntity()
        val mockTrainingSchedules = TrainingScheduleFixture.createMultipleEntityMocks(2)

        every { loginUserContext.getCurrentUser() } returns mockUser
        every { trainingScheduleQuerydsl.findByUser(mockUser) } returns mockTrainingSchedules

        // when
        val result = useCase.getTrainingSchedules()

        // then
        assertEquals(2, result.size)
        assertTrue(result.any { it.status == TrainingStatus.PLANNED })
        assertTrue(result.any { it.title == "훈련1" }) // 특정 순서에 종속되지 않고 포함만 되어있으면 검증 성공

        verify {
            loginUserContext.getCurrentUser()
            trainingScheduleQuerydsl.findByUser(mockUser)
            mockTrainingSchedules[0].toDto()
            mockTrainingSchedules[1].toDto()
        }

        confirmVerified(
            loginUserContext,
            trainingScheduleQuerydsl,
            *mockTrainingSchedules.toTypedArray()
        )
    }

    @Test
    @DisplayName("유저가 등록한 이번 주(일-토) 훈련일정 목록 가져오기")
    fun getCurrentlyPlannedTrainingSchedules() {
        // Given
        val currentDate = LocalDate.now()

        // Mock - 'getCurrentUser' 반환
        val mockUser = userEntity()
        every { loginUserContext.getCurrentUser() } returns mockUser

        // Mock - 'DateUtils.getWeekBoundaries' 함수 값 설정
        val previousSunday = DateUtils.findPreviousSunday(currentDate) // 지난 일요일
        val nextSaturday = DateUtils.findNextSaturday(currentDate) // 다음 토요일

        // Mock - 일정 반환될 리스트 및 각 엔티티 설정
        val mockSchedule1 = TrainingScheduleFixture.createEntityMock(
            id = 1L,
            scheduledDate = previousSunday.plusDays(1)
        )
        val mockSchedule2 = TrainingScheduleFixture.createEntityMock(
            id = 2L,
            scheduledDate = nextSaturday.minusDays(3)
        )
        val mockTrainingSchedules = listOf(mockSchedule1, mockSchedule2)

        // Mock - 'findCurrentWeekSchedulesByUser' 반환값
        every {
            trainingScheduleQuerydsl.findCurrentWeekSchedulesByUser(
                user = mockUser,
                previousSunday = previousSunday,
                nextSaturday = nextSaturday
            )
        } returns mockTrainingSchedules

        // When
        val result = useCase.getCurrentWeekTrainingSchedules()

        // Then
        // 반환된 사이즈와 값 검증
        assertEquals(result.size, 2)
        assertTrue(result.any { it.id == 1L })
        assertTrue(result.any { it.id == 2L })

        // Mock 호출 검증
        verify {
            loginUserContext.getCurrentUser()
            trainingScheduleQuerydsl.findCurrentWeekSchedulesByUser(
                user = mockUser,
                previousSunday = previousSunday,
                nextSaturday = nextSaturday
            )
            mockSchedule1.toDto()
            mockSchedule2.toDto()
        }
    }

    private fun userEntity() = UserFixture.createDefault(id = 1)

    private fun saveTrainingSchedule() = SaveTrainingSchedule(
        title = "템포런",
        location = "보라매공원",
        scheduledDate = LocalDate.now().plusDays(5),
        description = "빡세게 달려볼까",
        color = "#B5EAD7"
    )


}
