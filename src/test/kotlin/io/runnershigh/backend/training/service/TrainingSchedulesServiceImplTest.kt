package io.runnershigh.backend.training.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldExist
import io.kotest.matchers.shouldBe
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.runnershigh.backend.fixture.UserFixture
import io.runnershigh.backend.fixture.training.TrainingInfoFixture
import io.runnershigh.backend.fixture.training.TrainingScheduleFixture
import io.runnershigh.backend.training.dto.request.SaveTrainingGroup
import io.runnershigh.backend.training.dto.request.SaveTrainingItem
import io.runnershigh.backend.training.entity.*
import io.runnershigh.backend.training.exception.TrainingException
import io.runnershigh.backend.training.exception.TrainingExceptionType
import io.runnershigh.backend.training.mapper.toReadTrainingSchedule
import io.runnershigh.backend.training.repository.TrainingSchedulesRepository
import io.runnershigh.backend.user.entity.UserEntity
import io.runnershigh.backend.user.util.LoginUserContext
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration
import java.time.LocalDate


@ExtendWith(MockKExtension::class)
class TrainingSchedulesServiceImplTest : BehaviorSpec() {
    @MockK
    private lateinit var loginUserContext: LoginUserContext

    @MockK
    private lateinit var scheduleRepository: TrainingSchedulesRepository

    private lateinit var trainingSchedulesService: TrainingSchedulesService

    private fun userEntity(loginId: String) = UserFixture.createDefault(id = 1, loginId = loginId)

    init {
        beforeSpec {
            trainingSchedulesService =
                TrainingSchedulesServiceImpl(scheduleRepository, loginUserContext)
        }

        afterTest {
            clearMocks(scheduleRepository, loginUserContext)
            trainingSchedulesService =
                TrainingSchedulesServiceImpl(scheduleRepository, loginUserContext)
        }

        Given("유저가 올바른 훈련일정을 만든 상태에서") {
            val dto = TrainingInfoFixture.createSaveTrainingInfo()
            val user = userEntity("test1")
            val savedSchedule = TrainingInfoFixture.createTrainingSchedule(user = user)

            every { loginUserContext.getCurrentUser() } returns user
            every { scheduleRepository.save(any<TrainingSchedules>()) } returns savedSchedule

            When("훈련 생성을 수행하면") {
                val result = trainingSchedulesService.createTrainingSchedule(dto)

                Then("성공적으로 저장된다.") {
                    result shouldBe savedSchedule

                    verify(exactly = 1) {
                        loginUserContext.getCurrentUser()
                        scheduleRepository.save(match { schedule ->
                            schedule.title == dto.title &&
                                    schedule.location == dto.location &&
                                    schedule.scheduledDate == dto.scheduledDate &&
                                    schedule.description == dto.description &&
                                    schedule.user == user &&
                                    schedule.groups.isNotEmpty()
                        })
                    }

                    confirmVerified(loginUserContext, scheduleRepository)
                }
            }
        }
        Given("그룹 순서가 잘못된 훈련 일정을 만들고") {
            val dto = TrainingInfoFixture.createSaveTrainingInfo().copy(
                groups = listOf(
                    SaveTrainingGroup(
                        groupOrder = 1,
                        repeatCount = 1,
                        description = "그룹1",
                        items = listOf(TrainingInfoFixture.createSaveTrainingItems(1)[0])
                    ),
                    SaveTrainingGroup(
                        groupOrder = 3, // 2가 아닌 3으로 설정 (순서 오류)
                        repeatCount = 1,
                        description = "그룹2",
                        items = listOf(TrainingInfoFixture.createSaveTrainingItems(1)[0])
                    )
                )
            )
            val user = userEntity("test2")

            every { loginUserContext.getCurrentUser() } returns user

            When("훈련 일정을 생성하려고 하면") {
                Then("그룹 순서 오류 예외가 발생한다") {
                    shouldThrow<TrainingException> {
                        trainingSchedulesService.createTrainingSchedule(dto)
                    }.run {
                        exceptionType shouldBe TrainingExceptionType.INVALID_GROUP_ORDER
                        message shouldBe "그룹 순서는 1부터 연속적이어야 합니다."
                    }

                    verify(exactly = 0) {
                        scheduleRepository.save(any())
                    }
                }
            }
        }

        Given("아이템 순서가 잘못된 훈련 일정을 만들고") {
            val dto = TrainingInfoFixture.createSaveTrainingInfo().copy(
                groups = listOf(
                    SaveTrainingGroup(
                        groupOrder = 1,
                        repeatCount = 1,
                        description = "그룹1",
                        items = listOf(
                            SaveTrainingItem(
                                itemOrder = 1,
                                targetType = TargetType.DISTANCE,
                                targetMinPace = Duration.ofMinutes(4).plusSeconds(30),
                                targetMaxPace = Duration.ofMinutes(5),
                                targetAvgPace = Duration.ofMinutes(4).plusSeconds(45),
                                runningTypeCode = 1,
                                distanceUnit = DistanceUnit.KILOMETER,
                                targetDistance = 5.0,
                                targetTime = Duration.ofMinutes(25),
                                estimatedDistance = 5.0,
                                estimatedTime = Duration.ofMinutes(25),
                                note = "아이템1"
                            ),
                            SaveTrainingItem(
                                itemOrder = 3, // 2가 아닌 3으로 설정 (순서 오류)
                                targetType = TargetType.DISTANCE,
                                targetMinPace = Duration.ofMinutes(4).plusSeconds(30),
                                targetMaxPace = Duration.ofMinutes(5),
                                targetAvgPace = Duration.ofMinutes(4).plusSeconds(45),
                                runningTypeCode = 1,
                                distanceUnit = DistanceUnit.KILOMETER,
                                targetDistance = 5.0,
                                targetTime = Duration.ofMinutes(25),
                                estimatedDistance = 5.0,
                                estimatedTime = Duration.ofMinutes(25),
                                note = "아이템2"
                            )
                        )
                    )
                )
            )
            val user = userEntity("test3")

            every { loginUserContext.getCurrentUser() } returns user

            When("훈련 일정을 생성하려고 하면") {
                Then("아이템 순서 오류 예외가 발생한다") {
                    shouldThrow<TrainingException> {
                        trainingSchedulesService.createTrainingSchedule(dto)
                    }.run {
                        exceptionType shouldBe TrainingExceptionType.INVALID_ITEM_ORDER
                        message shouldBe "아이템 순서는 1부터 연속적이어야 합니다."
                    }

                    verify(exactly = 0) {
                        scheduleRepository.save(any())
                    }
                }
            }
        }

        Given("최소페이스가 평균페이스보다 빠른 잘못된 훈련 일정을 만들고") {
            val dto = TrainingInfoFixture.createSaveTrainingInfo().copy(
                groups = listOf(
                    SaveTrainingGroup(
                        groupOrder = 1,
                        repeatCount = 1,
                        description = "그룹1",
                        items = listOf(
                            TrainingInfoFixture.createSaveTrainingPlanItem(
                                itemOrder = 1,
                                targetMinPace = Duration.ofMinutes(4)
                                    .plusSeconds(30), // 최소페이스가 평균보다 빠름 -> 검증실패유도
                                targetMaxPace = Duration.ofMinutes(3),
                                targetAvgPace = Duration.ofMinutes(4).plusSeconds(45)
                            ),
                        )
                    )
                )
            )
            val user = userEntity("test4")

            every { loginUserContext.getCurrentUser() } returns user

            When("훈련 일정을 생성하려고 하면") {
                Then("페이스 범위 오류 예외가 발생한다") {
                    shouldThrow<TrainingException> {
                        trainingSchedulesService.createTrainingSchedule(dto)
                    }.run {
                        exceptionType shouldBe TrainingExceptionType.INVALID_PACE_RANGE
                        message shouldBe "페이스 범위가 올바르지 않습니다. (최소 ≤ 평균 ≤ 최대)"
                    }

                    verify(exactly = 0) {
                        scheduleRepository.save(any())
                    }
                }
            }
        }

        Given("예상 거리가 100km를 초과하는 훈련 계획을 만들고") {
            val dto = TrainingInfoFixture.createSaveTrainingInfo().copy(
                groups = listOf(
                    SaveTrainingGroup(
                        groupOrder = 1,
                        repeatCount = 1,
                        description = "그룹1",
                        items = listOf(
                            TrainingInfoFixture.createSaveTrainingPlanItem(
                                itemOrder = 1,
                                estimatedDistance = 101.0
                            ),
                        )
                    )
                )
            )
            val user = userEntity("test5")

            every { loginUserContext.getCurrentUser() } returns user

            When("훈련 일정을 생성하려고 하면") {
                Then("거리 제한 초과 예외가 발생한다") {
                    shouldThrow<TrainingException> {
                        trainingSchedulesService.createTrainingSchedule(dto)
                    }.run {
                        exceptionType shouldBe TrainingExceptionType.TRAINING_DISTANCE_LIMIT_EXCEEDED
                        message shouldBe "총 훈련 거리는 100km를 초과할 수 없습니다."
                    }

                    verify(exactly = 0) {
                        scheduleRepository.save(any())
                    }
                }
            }
        }

        Given("1년 이후의 날짜로 훈련 일정 데이터를 만들고") {
            val futureDate = LocalDate.now().plusDays(366)
            val dto = TrainingInfoFixture.createSaveTrainingInfo().copy(scheduledDate = futureDate)
            val user = userEntity("test6")

            every { loginUserContext.getCurrentUser() } returns user

            When("훈련 일정을 생성하려고 하면") {
                Then("날짜 제한 예외가 발생한다") {
                    shouldThrow<TrainingException> {
                        trainingSchedulesService.createTrainingSchedule(dto)
                    }.run {
                        exceptionType shouldBe TrainingExceptionType.CANNOT_REGISTER_TRAINING_BEYOND_ONE_YEAR
                        message shouldBe "최대 1년까지만 훈련 일정을 등록할 수 있습니다."
                    }

                    verify(exactly = 0) {
                        scheduleRepository.save(any())
                    }
                }
            }
        }

        // 기존 조회 관련 테스트들...
        Given("유저가 등록한 훈련일정이 있을때") {
            val mockUser = userEntity("test7")
            val mockTrainingSchedules =
                TrainingInfoFixture.createMultipleEntityMocks(2, mockUser)

            every { loginUserContext.getCurrentUser() } returns mockUser
            every { scheduleRepository.retrieveTrainingSchedules(mockUser) } returns mockTrainingSchedules

            When("정상적으로 조회를 성공하면") {
                val result = trainingSchedulesService.getTrainingSchedules()

                Then("훈련 일정 목록을 가져온다.") {
                    result.size shouldBe 2
                    result shouldExist { it.status == TrainingStatus.PLANNED }

                    verify {
                        loginUserContext.getCurrentUser()
                        scheduleRepository.retrieveTrainingSchedules(mockUser)
                        mockTrainingSchedules[0].toReadTrainingSchedule()
                        mockTrainingSchedules[1].toReadTrainingSchedule()
                    }

                    confirmVerified(
                        loginUserContext,
                        scheduleRepository,
                        *mockTrainingSchedules.toTypedArray()
                    )
                }
            }
        }

        Given("다음 예정된 훈련 일정이 있는 상태에서") {
            val mockUser = userEntity("test8")
            val futureDate = LocalDate.now().plusDays(3)

            val mockTrainingSchedule = TrainingInfoFixture.createEntityMockWithItems(
                id = 1L,
                title = "템포런 훈련",
                scheduledDate = futureDate,
                user = mockUser
            )

            every { loginUserContext.getCurrentUser() } returns mockUser
            every { scheduleRepository.retrieveNextUpcomingSchedule(mockUser) } returns mockTrainingSchedule

            When("다음 예정된 훈련 일정을 조회하면") {
                val result = trainingSchedulesService.getNextUpcomingTrainingSchedule()

                Then("다음 훈련 일정 정보를 반환한다") {
                    result?.scheduleId shouldBe 1L
                    result?.title shouldBe "템포런 훈련"
                    result?.scheduledDate shouldBe futureDate
                    result?.totalDistance shouldBe 10.0
                    result?.totalTime shouldBe Duration.ofMinutes(50)

                    verify {
                        loginUserContext.getCurrentUser()
                        scheduleRepository.retrieveNextUpcomingSchedule(mockUser)
                        mockTrainingSchedule.id
                        mockTrainingSchedule.title
                        mockTrainingSchedule.scheduledDate
                        mockTrainingSchedule.groups
                    }

                    confirmVerified(loginUserContext, scheduleRepository, mockTrainingSchedule)
                }
            }
        }

        Given("이번 주 훈련 요약 API가 호출될때") {
            val mockUser = mockk<UserEntity>()

            When("이번 주 훈련 일정이 없는 경우") {
                every { loginUserContext.getCurrentUser() } returns mockUser
                every {
                    scheduleRepository.findThisWeekTrainingSchedules(
                        mockUser,
                        any(),
                        any(),
                        plannedOnly = true
                    )
                } returns emptyList()

                Then("모든 값이 0인 요약 정보를 반환한다.") {
                    val result = trainingSchedulesService.getSummaryThisWeekForSchedule()
                    val dto = TrainingScheduleFixture.createMockSummaryThisWeekSchedule(
                        scheduleCount = 0,
                        totalDistance = 0.0,
                        totalTime = Duration.ZERO
                    )
                    result shouldBe dto

                    verify { loginUserContext.getCurrentUser() }
                    verify {
                        scheduleRepository.findThisWeekTrainingSchedules(
                            mockUser,
                            any(),
                            any(),
                            plannedOnly = true
                        )
                    }
                }
            }

            When("이번 주 훈련 일정이 여러개 있는 경우") {
                val mockSchedule1 = TrainingScheduleFixture.createMockTrainingSchedule(
                    estimatedDistance = 10.0,
                    estimatedTime = Duration.ofMinutes(50)
                )
                val mockSchedule2 = TrainingScheduleFixture.createMockTrainingSchedule(
                    estimatedDistance = 15.0,
                    estimatedTime = Duration.ofMinutes(90)
                )
                val mockSchedule3 = TrainingScheduleFixture.createMockTrainingSchedule(
                    estimatedDistance = 5.0,
                    estimatedTime = Duration.ofMinutes(30)
                )
                val scheduleList = listOf(mockSchedule1, mockSchedule2, mockSchedule3)

                every { loginUserContext.getCurrentUser() } returns mockUser
                every {
                    scheduleRepository.findThisWeekTrainingSchedules(
                        mockUser,
                        any(),
                        any(),
                        plannedOnly = true
                    )
                } returns scheduleList

                Then("모든 일정의 합계를 반환한다") {
                    val result = trainingSchedulesService.getSummaryThisWeekForSchedule()
                    val dto = TrainingScheduleFixture.createMockSummaryThisWeekSchedule(
                        scheduleCount = scheduleList.size,
                        totalDistance = 30.0,
                        totalTime = Duration.ofMinutes(170)
                    )
                    result shouldBe dto
                }
            }

            When("훈련 일정에 여러 그룹과 아이템이 있는 경우") {
                val mockItem1 = mockk<TrainingPlanItems>()
                val mockItem2 = mockk<TrainingPlanItems>()
                val mockItem3 = mockk<TrainingPlanItems>()

                every { mockItem1.estimatedDistance } returns 10.0
                every { mockItem1.estimatedTime } returns Duration.ofMinutes(50)
                every { mockItem2.estimatedDistance } returns 15.0
                every { mockItem2.estimatedTime } returns Duration.ofMinutes(90)
                every { mockItem3.estimatedDistance } returns 5.0
                every { mockItem3.estimatedTime } returns Duration.ofMinutes(30)

                val mockGroup1 = mockk<TrainingPlanGroups>()
                val mockGroup2 = mockk<TrainingPlanGroups>()

                every { mockGroup1.items } returns mutableListOf(mockItem1, mockItem2)
                every { mockGroup2.items } returns mutableListOf(mockItem3)

                val mockSchedule = mockk<TrainingSchedules>()
                every { mockSchedule.groups } returns mutableListOf(mockGroup1, mockGroup2)

                every { loginUserContext.getCurrentUser() } returns mockUser
                every {
                    scheduleRepository.findThisWeekTrainingSchedules(
                        mockUser,
                        any(),
                        any(),
                        plannedOnly = true
                    )
                } returns listOf(mockSchedule)

                Then("모든 일정의 합계를 반환한다") {
                    val result = trainingSchedulesService.getSummaryThisWeekForSchedule()
                    val dto = TrainingScheduleFixture.createMockSummaryThisWeekSchedule(
                        scheduleCount = 1,
                        totalDistance = 30.0,
                        totalTime = Duration.ofMinutes(170)
                    )
                    result shouldBe dto
                }
            }
        }

        Given("이번 주 훈련일정을 조회하려고 할때") {
            val mockUser = userEntity("test9")

            When("이번 주 훈련일정이 없는 경우") {
                every { loginUserContext.getCurrentUser() } returns mockUser
                every {
                    scheduleRepository.findThisWeekTrainingSchedules(
                        mockUser,
                        any(),
                        any()
                    )
                } returns emptyList()

                val result = trainingSchedulesService.getThisWeekTrainingSchedules()

                Then("빈 리스트를 반환한다") {
                    result shouldBe emptyList()

                    verify {
                        loginUserContext.getCurrentUser()
                        scheduleRepository.findThisWeekTrainingSchedules(
                            mockUser,
                            any(),
                            any()
                        )
                    }
                }
            }

            When("이번 주 훈련일정이 여러개 있는 경우") {
                val mockSchedule1 = TrainingScheduleFixture.createMockTrainingScheduleForWeekly(
                    id = 1L,
                    title = "즐겁게 월요런",
                    scheduledDate = LocalDate.now().minusDays(3)
                )
                val mockSchedule2 = TrainingScheduleFixture.createMockTrainingScheduleForWeekly(
                    id = 2L,
                    title = "템포런 훈련",
                    scheduledDate = LocalDate.now()
                )

                every { loginUserContext.getCurrentUser() } returns mockUser
                every {
                    scheduleRepository.findThisWeekTrainingSchedules(
                        mockUser,
                        any(),
                        any()
                    )
                } returns listOf(mockSchedule1, mockSchedule2)

                val result = trainingSchedulesService.getThisWeekTrainingSchedules()

                Then("훈련일정 목록을 WeeklyTrainingSchedule 형태로 반환한다") {
                    result.size shouldBe 2

                    result[0].run {
                        scheduleId shouldBe 1L
                        title shouldBe "즐겁게 월요런"
                        scheduledDate shouldBe LocalDate.now().minusDays(3)
                    }

                    result[1].run {
                        scheduleId shouldBe 2L
                        title shouldBe "템포런 훈련"
                        scheduledDate shouldBe LocalDate.now()
                    }

                    verify {
                        loginUserContext.getCurrentUser()
                        scheduleRepository.findThisWeekTrainingSchedules(
                            mockUser,
                            any(),
                            any()
                        )
                    }

                    confirmVerified(loginUserContext, scheduleRepository)
                }
            }
        }
    }
}


