package io.runnershigh.backend.training.application.command

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.runnershigh.backend.fixture.TrainingScheduleFixture
import io.runnershigh.backend.training.domain.enum.DistanceUnit
import io.runnershigh.backend.training.domain.enum.TargetType
import io.runnershigh.backend.training.domain.mapper.toEntity
import io.runnershigh.backend.training.infrastructure.entity.TrainingPlanItems
import io.runnershigh.backend.training.infrastructure.repository.TrainingPlanItemsRepository
import io.runnershigh.backend.training.infrastructure.repository.TrainingSchedulesRepository
import io.runnershigh.backend.training.ui.dto.request.SaveTrainingPlanItem
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalTime

@ExtendWith(MockKExtension::class)
class TrainingPlanItemsUseCaseTest {

    @MockK
    private lateinit var planItemsRepository: TrainingPlanItemsRepository

    @MockK
    private lateinit var scheduleRepository: TrainingSchedulesRepository

    private lateinit var useCase: TrainingPlanItemsUseCase

    @BeforeEach
    fun setup() {
        useCase = TrainingPlanItemsUseCase(planItemsRepository, scheduleRepository)
    }

    @Test
    @DisplayName("훈련 상세계획 정상으로 저장")
    fun createTrainingPlanItem() {
        //given
        val scheduleId = 1L
        val scheduleEntity = TrainingScheduleFixture.createDefault(id = scheduleId)
        val planItemDto = makeSaveTrainingPlanItemDto()

        every { scheduleRepository.findByIdOrNull(scheduleId) } returns scheduleEntity
        every { planItemsRepository.save(ofType<TrainingPlanItems>()) } returns planItemDto.toEntity(
            scheduleEntity
        )

        //when
        val result = useCase.createTrainingItems(scheduleId, planItemDto)

        //then
        assertEquals(planItemDto.toEntity(scheduleEntity).itemOrder, result.itemOrder)
        verify { scheduleRepository.findByIdOrNull(scheduleId) }
        verify { planItemsRepository.save(ofType<TrainingPlanItems>()) }
    }

    private fun makeSaveTrainingPlanItemDto() = SaveTrainingPlanItem(
        itemOrder = 1,
        targetType = TargetType.DISTANCE,  // Replace with an appropriate value
        targetMinPace = LocalTime.of(0, 5, 0),  // Example time
        targetMaxPace = LocalTime.of(0, 6, 0),  // Example time
        targetAvgPace = LocalTime.of(0, 5, 30),  // Example time
        runningTypeCode = 101,  // Example code
        distanceUnit = DistanceUnit.KILOMETER,  // Replace with an appropriate value
        targetDistance = 10.0,  // Example distance in km
        targetTime = LocalTime.of(0, 50, 0),  // Example target time
        estimatedDistance = 10.5,  // Example estimated distance
        estimatedTime = LocalTime.of(0, 55, 0),  // Example estimated time
        note = "Training notes example"
    )

}