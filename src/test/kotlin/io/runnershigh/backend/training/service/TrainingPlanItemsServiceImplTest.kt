package io.runnershigh.backend.training.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.runnershigh.backend.fixture.training.TrainingPlanFixture
import io.runnershigh.backend.fixture.training.TrainingScheduleFixture
import io.runnershigh.backend.training.entity.TrainingPlanItems
import io.runnershigh.backend.training.mapper.toEntity
import io.runnershigh.backend.training.repository.TrainingPlanItemsRepository
import io.runnershigh.backend.training.repository.TrainingSchedulesRepository
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull

@ExtendWith(MockKExtension::class)
class TrainingPlanItemsServiceImplTest : BehaviorSpec() {
    @MockK
    private lateinit var planItemsRepository: TrainingPlanItemsRepository

    @MockK
    private lateinit var scheduleRepository: TrainingSchedulesRepository

    private lateinit var planItemsService: TrainingPlanItemsServiceImpl

    init {
        beforeSpec {
            planItemsService = TrainingPlanItemsServiceImpl(planItemsRepository, scheduleRepository)
        }
        afterTest {
            clearMocks(planItemsRepository, scheduleRepository)
            planItemsService = TrainingPlanItemsServiceImpl(planItemsRepository, scheduleRepository)
        }
        Given("훈련 상세 계획을 정상적으로 입력하고") {
            val scheduleId = 1L
            val scheduleEntity = TrainingScheduleFixture.createDefault(id = scheduleId)
            val planItemDto = TrainingPlanFixture.createSaveTrainingPlanItemDto()

            every { scheduleRepository.findByIdOrNull(scheduleId) } returns scheduleEntity
            every { planItemsRepository.save(ofType<TrainingPlanItems>()) } returns planItemDto.toEntity(
                scheduleEntity
            )

            When("생성을 시도하면") {
                val result = planItemsService.createTrainingItems(scheduleId, planItemDto)

                Then("저장을 성공한다.") {
                    result.itemOrder shouldBe planItemDto.toEntity(scheduleEntity).itemOrder
                    verify { scheduleRepository.findByIdOrNull(scheduleId) }
                    verify { planItemsRepository.save(ofType<TrainingPlanItems>()) }
                }
            }
        }
    }


}