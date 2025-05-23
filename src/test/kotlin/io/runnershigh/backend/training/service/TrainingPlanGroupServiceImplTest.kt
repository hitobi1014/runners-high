package io.runnershigh.backend.training.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.runnershigh.backend.fixture.training.TrainingScheduleFixture
import io.runnershigh.backend.training.dto.request.SaveTrainingPlanGroup
import io.runnershigh.backend.training.entity.TrainingPlanGroups
import io.runnershigh.backend.training.exception.TrainingException
import io.runnershigh.backend.training.exception.TrainingExceptionType
import io.runnershigh.backend.training.mapper.toEntity
import io.runnershigh.backend.training.repository.TrainingPlanGroupRepository
import io.runnershigh.backend.training.repository.TrainingSchedulesRepository
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull

@ExtendWith(MockKExtension::class)
class TrainingPlanGroupServiceImplTest : BehaviorSpec() {
    @MockK
    private lateinit var planGroupRepository: TrainingPlanGroupRepository

    @MockK
    private lateinit var schedulesRepository: TrainingSchedulesRepository

    private lateinit var trainingPlanGroupService: TrainingPlanGroupServiceImpl

    private fun getSaveTrainingPlanGroupDto(): SaveTrainingPlanGroup = SaveTrainingPlanGroup(
        groupOrder = 1,
        repeatCount = 2,
        description = "템포런"
    )

    init {
        beforeSpec {
            trainingPlanGroupService =
                TrainingPlanGroupServiceImpl(planGroupRepository, schedulesRepository)
        }

        afterTest {
            clearMocks(planGroupRepository, schedulesRepository)
            trainingPlanGroupService =
                TrainingPlanGroupServiceImpl(planGroupRepository, schedulesRepository)
        }

        Given("훈련 그룹 정보를 정상적으로 입력하고") {
            val scheduleId: Long = 1
            val scheduleEntity = TrainingScheduleFixture.createDefault(id = scheduleId)
            val dto = getSaveTrainingPlanGroupDto()
            val trainingGroupEntity = dto.toEntity(scheduleEntity)

            every { schedulesRepository.findByIdOrNull(ofType<Long>()) } returns scheduleEntity
            every { planGroupRepository.save(ofType<TrainingPlanGroups>()) } returns trainingGroupEntity

            When("그룹 생성을 수행하면") {
                val result = trainingPlanGroupService.createTrainingPlanGroup(dto, scheduleId)

                Then("그룹이 생성된다.") {
                    result shouldNotBe null
                    result.groupOrder shouldBe trainingGroupEntity.groupOrder
                    result.repeatCount shouldBe trainingGroupEntity.repeatCount

                    verify(exactly = 1) { schedulesRepository.findByIdOrNull(ofType<Long>()) }
                    verify(exactly = 1) { planGroupRepository.save(ofType<TrainingPlanGroups>()) }

                    confirmVerified(schedulesRepository, planGroupRepository)
                }
            }
        }

        Given("훈련 그룹 정보를 입력학고") {
            val scheduleId: Long = 1
            val dto = getSaveTrainingPlanGroupDto()

            every { schedulesRepository.findByIdOrNull(ofType<Long>()) } returns null

            When("생성을 하기 위해 훈련정보를 일정ID로 찾았을때 값이 훈련 일정이 없으면") {
                Then("예외가 발생한다.") {
                    shouldThrow<TrainingException> {
                        trainingPlanGroupService.createTrainingPlanGroup(dto, scheduleId)
                    }.run {
                        exceptionType shouldBe TrainingExceptionType.CANNOT_FOUND_TRAINING_SCHEDULE
                        message shouldBe "해당 정보로 등록된 훈련 일정을 찾을 수 없습니다."
                    }
                    verify(exactly = 1) { schedulesRepository.findByIdOrNull(ofType<Long>()) }
                    confirmVerified(schedulesRepository)
                }
            }
        }
    }

}