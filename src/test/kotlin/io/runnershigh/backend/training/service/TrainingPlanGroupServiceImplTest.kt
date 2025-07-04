package io.runnershigh.backend.training.service

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearMocks
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.runnershigh.backend.training.repository.TrainingPlanGroupRepository
import io.runnershigh.backend.training.repository.TrainingSchedulesRepository
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class TrainingPlanGroupServiceImplTest : BehaviorSpec() {
    @MockK
    private lateinit var planGroupRepository: TrainingPlanGroupRepository

    @MockK
    private lateinit var schedulesRepository: TrainingSchedulesRepository

    private lateinit var trainingPlanGroupService: TrainingPlanGroupServiceImpl

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

        /*
        * 250705: 현재 기준에서는 훈련 그룹을 독단적으로 저장하는 경우가 없음
        * 추후 독립적으로 저장하는 경우가 생기면 '훈련그룹 저장' 테스트 수행
        * */
//        Given("훈련 그룹 정보를 정상적으로 입력하고") {
//            val scheduleId: Long = 1
//            val scheduleEntity = TrainingInfoFixture.createDefault(id = scheduleId)
//            val dto = TrainingInfoFixture.createSaveTrainingGroup(groupIndex = 1, itemsPerGroup = 3)
//            val trainingGroupEntity = dto.toEntity(scheduleEntity)
//
//            every { schedulesRepository.findByIdOrNull(ofType<Long>()) } returns scheduleEntity
//            every { planGroupRepository.save(ofType<TrainingPlanGroups>()) } returns trainingGroupEntity
//
//            When("그룹 생성을 수행하면") {
//                val result = trainingPlanGroupService.createTrainingPlanGroup(dto, scheduleId)
//
//                Then("그룹이 생성된다.") {
//                    result shouldNotBe null
//                    result.groupOrder shouldBe trainingGroupEntity.groupOrder
//                    result.repeatCount shouldBe trainingGroupEntity.repeatCount
//
//                    verify(exactly = 1) { schedulesRepository.findByIdOrNull(ofType<Long>()) }
//                    verify(exactly = 1) { planGroupRepository.save(ofType<TrainingPlanGroups>()) }
//
//                    confirmVerified(schedulesRepository, planGroupRepository)
//                }
//            }
//        }

//        Given("훈련 그룹 정보를 입력학고") {
//            val scheduleId: Long = 1
//            val dto = getSaveTrainingPlanGroupDto()
//
//            every { schedulesRepository.findByIdOrNull(ofType<Long>()) } returns null
//
//            When("생성을 하기 위해 훈련정보를 일정ID로 찾았을때 값이 훈련 일정이 없으면") {
//                Then("예외가 발생한다.") {
//                    shouldThrow<TrainingException> {
//                        trainingPlanGroupService.createTrainingPlanGroup(dto, scheduleId)
//                    }.run {
//                        exceptionType shouldBe TrainingExceptionType.CANNOT_FOUND_TRAINING_SCHEDULE
//                        message shouldBe "해당 정보로 등록된 훈련 일정을 찾을 수 없습니다."
//                    }
//                    verify(exactly = 1) { schedulesRepository.findByIdOrNull(ofType<Long>()) }
//                    confirmVerified(schedulesRepository)
//                }
//            }
//        }
    }

}