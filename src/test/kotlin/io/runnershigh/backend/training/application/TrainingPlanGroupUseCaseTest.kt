package io.runnershigh.backend.training.application

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.runnershigh.backend.fixture.TrainingScheduleFixture
import io.runnershigh.backend.training.domain.mapper.toEntity
import io.runnershigh.backend.training.exception.TrainingException
import io.runnershigh.backend.training.exception.TrainingExceptionType
import io.runnershigh.backend.training.infrastructure.entity.TrainingPlanGroups
import io.runnershigh.backend.training.infrastructure.repository.TrainingPlanGroupRepository
import io.runnershigh.backend.training.infrastructure.repository.TrainingSchedulesRepository
import io.runnershigh.backend.training.ui.dto.SaveTrainingPlanGroup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull

@ExtendWith(MockKExtension::class)
class TrainingPlanGroupUseCaseTest {

    @MockK
    private lateinit var trainingPlanGroupRepository: TrainingPlanGroupRepository

    @MockK
    private lateinit var trainingSchedulesRepository: TrainingSchedulesRepository

    private lateinit var useCase: TrainingPlanGroupUseCase

    @BeforeEach
    fun setup() {
        useCase = TrainingPlanGroupUseCase(trainingPlanGroupRepository, trainingSchedulesRepository)
    }

    @Test
    @DisplayName("훈련 그룹을 정상으로 저장한다.")
    fun createTrainingPlanGroup() {
        //given
        val scheduleId: Long = 1
        val scheduleEntity = TrainingScheduleFixture.createDefault(id = scheduleId)
        val dto = getSaveTrainingPlanGroupDto()
        val trainingGroupEntity = dto.toEntity(scheduleEntity)

        //when
        every { trainingSchedulesRepository.findByIdOrNull(ofType<Long>()) } returns scheduleEntity
        every { trainingPlanGroupRepository.save(ofType<TrainingPlanGroups>()) } returns trainingGroupEntity

        val result = useCase.createTrainingPlanGroup(dto, scheduleId)

        //then
        assertNotNull(result)
        assertEquals(trainingGroupEntity.groupOrder, result.groupOrder)
        assertEquals(trainingGroupEntity.repeatCount, result.repeatCount)

        verify(exactly = 1) { trainingSchedulesRepository.findByIdOrNull(ofType<Long>()) }
        verify(exactly = 1) { trainingPlanGroupRepository.save(ofType<TrainingPlanGroups>()) }

        // 모든 Mock 호출 확인
        confirmVerified(trainingSchedulesRepository, trainingPlanGroupRepository)
    }

    @Test
    @DisplayName("훈련 그룹 저장 - 훈련일정 못찾는 경우 예외 발생")
    fun createTrainingPlanGroup_withInvalidScheduleId() {
        //given
        val scheduleId: Long = 1
        val dto = getSaveTrainingPlanGroupDto()

        every { trainingSchedulesRepository.findByIdOrNull(ofType<Long>()) } returns null

        //when
        val exception = assertThrows<TrainingException> {
            useCase.createTrainingPlanGroup(dto, scheduleId)
        }

        //then
        assertEquals(TrainingExceptionType.CANNOT_FOUND_TRAINING_SCHEDULE, exception.exceptionType)
        verify(exactly = 1) { trainingSchedulesRepository.findByIdOrNull(ofType<Long>()) }
        confirmVerified(trainingSchedulesRepository)
    }

    private fun getSaveTrainingPlanGroupDto(): SaveTrainingPlanGroup = SaveTrainingPlanGroup(
        groupOrder = 1,
        repeatCount = 2,
        description = "템포런"
    )


}