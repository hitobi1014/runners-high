package io.runnershigh.backend.training.service.command

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldExist
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.runnershigh.backend.fixture.TrainingScheduleFixture
import io.runnershigh.backend.fixture.UserFixture
import io.runnershigh.backend.shared.util.DateUtils
import io.runnershigh.backend.training.dto.request.SaveTrainingSchedule
import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.training.entity.enum.TrainingStatus
import io.runnershigh.backend.training.exception.TrainingException
import io.runnershigh.backend.training.exception.TrainingExceptionType
import io.runnershigh.backend.training.mapper.toDto
import io.runnershigh.backend.training.mapper.toEntity
import io.runnershigh.backend.training.repository.TrainingSchedulesRepository
import io.runnershigh.backend.training.service.TrainingSchedulesService
import io.runnershigh.backend.training.service.TrainingSchedulesServiceImpl
import io.runnershigh.backend.user.util.LoginUserContext
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate


@ExtendWith(MockKExtension::class)
class TrainingSchedulesServiceImplTest : BehaviorSpec() {
    @MockK
    private lateinit var loginUserContext: LoginUserContext

    @MockK
    private lateinit var repository: TrainingSchedulesRepository

    private lateinit var trainingSchedulesService: TrainingSchedulesService

    private fun userEntity(loginId: String) = UserFixture.createDefault(id = 1, loginId = loginId)

    private fun saveTrainingSchedule() = SaveTrainingSchedule(
        title = "템포런",
        location = "보라매공원",
        scheduledDate = LocalDate.now().plusDays(5),
        description = "빡세게 달려볼까",
        color = "#B5EAD7"
    )

    init {
        beforeSpec {
            trainingSchedulesService =
                TrainingSchedulesServiceImpl(repository, loginUserContext)
        }

        afterTest {
            clearMocks(repository, loginUserContext)
            trainingSchedulesService =
                TrainingSchedulesServiceImpl(repository, loginUserContext)
        }

        Given("유저가 올바른 훈련일정을 만든 상태에서") {
            val dto = saveTrainingSchedule()
            val user = userEntity("test1")
            every { loginUserContext.getCurrentUser() } returns user
            every { repository.save(ofType<TrainingSchedules>()) } returns dto.toEntity(user)

            When("훈련 생성을 수행하면") {
                trainingSchedulesService.createTrainingSchedule(dto)

                Then("성공적으로 저장된다.") {
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
            }
        }

        Given("과거 날짜의 훈련 일정 데이터를 만들고") {
            val pastDate = LocalDate.now().minusDays(1)
            val dto = saveTrainingSchedule().copy(scheduledDate = pastDate, title = "회복")
            val user = userEntity("test2")

            every { loginUserContext.getCurrentUser() } returns user

            When("해당 데이터로 훈련 일정을 생성하려고 하면") {
                Then("과거 날짜 등록이 불가능하다는 예외가 발생한다") {
                    shouldThrow<TrainingException> {
                        trainingSchedulesService.createTrainingSchedule(dto)
                    }.run {
                        exceptionType shouldBe TrainingExceptionType.CANNOT_REGISTER_PAST_TRAINING
                        message shouldBe "과거 시간에는 훈련을 등록할 수 없습니다."
                    }
                    verify(exactly = 0) {
                        repository.save(any())
                    }
                }
            }
        }
        Given("1년 이후의 날짜로 훈련 일정 데이터를 만들고") {
            val futureDate = LocalDate.now().plusDays(366)
            val dto = saveTrainingSchedule().copy(scheduledDate = futureDate, title = "마라톤")
            val user = userEntity("test3")

            every { loginUserContext.getCurrentUser() } returns user

            When("훈련 일정을 생성하려고 하면") {
                Then("예외가 발생한다.")
                shouldThrow<TrainingException> {
                    trainingSchedulesService.createTrainingSchedule(dto)
                }.run {
                    exceptionType shouldBe TrainingExceptionType.CANNOT_REGISTER_TRAINING_BEYOND_ONE_YEAR
                    message shouldBe "최대 1년까지만 훈련 일정을 등록할 수 있습니다."
                }
                verify(exactly = 0) {
                    repository.save(any())
                }
            }
        }

        Given("당일 훈련일정을 데이터를 만들고") {
            val currentDate = LocalDate.now()
            val dto = saveTrainingSchedule().copy(scheduledDate = currentDate)
            val user = userEntity("test3")

            every { loginUserContext.getCurrentUser() } returns user
            every { repository.save(ofType<TrainingSchedules>()) } returns dto.toEntity(user)

            When("해당 데이터로 훈련 일정을 생성하면") {
                trainingSchedulesService.createTrainingSchedule(dto)
                Then("정상적으로 저장된다.") {
                    verify(exactly = 1) {
                        repository.save(any())
                    }
                }
            }
        }

        Given("유저가 등록한 훈련일정이 있을때") {
            val mockUser = userEntity("test4")
            val mockTrainingSchedules =
                TrainingScheduleFixture.createMultipleEntityMocks(2, mockUser)

            every { loginUserContext.getCurrentUser() } returns mockUser
            every { repository.retrieveTrainingSchedules(mockUser) } returns mockTrainingSchedules

            When("정상적으로 조회를 성공하면") {
                val result = trainingSchedulesService.getTrainingSchedules()

                Then("훈련 일정 목록을 가져온다.") {
                    result.size shouldBe 2
                    result shouldExist { it.status == TrainingStatus.PLANNED }

                    verify {
                        loginUserContext.getCurrentUser()
                        repository.retrieveTrainingSchedules(mockUser)
                        mockTrainingSchedules[0].toDto()
                        mockTrainingSchedules[1].toDto()
                    }

                    confirmVerified(
                        loginUserContext,
                        repository,
                        *mockTrainingSchedules.toTypedArray()
                    )
                }
            }
        }

        Given("유저가 등록한 일정이 여러개 있을때") {
            val currentDate = LocalDate.now()
            val mockUser = userEntity("test5")

            every { loginUserContext.getCurrentUser() } returns mockUser

            val previousSunday = DateUtils.findPreviousSunday(currentDate)
            val nextSaturday = DateUtils.findNextSaturday(currentDate)

            val mockSchedule1 = TrainingScheduleFixture.createEntityMock(
                id = 1L,
                scheduledDate = previousSunday.plusDays(1),
                user = mockUser,
            )
            val mockSchedule2 = TrainingScheduleFixture.createEntityMock(
                id = 2L,
                scheduledDate = nextSaturday.minusDays(3),
                user = mockUser,
            )
            val mockTrainingSchedules = listOf(mockSchedule1, mockSchedule2)

            every {
                repository.retrieveCurrentWeekSchedules(
                    user = mockUser,
                    previousSunday = previousSunday,
                    nextSaturday = nextSaturday
                )
            } returns mockTrainingSchedules

            When("이번 주 훈련일정 목록 조회를 하면") {
                val result = trainingSchedulesService.getCurrentWeekTrainingSchedules()

                Then("일요일부터 토요일까지 등록된 훈련목록을 가져온다.") {
                    result.size shouldBe 2
                    result.map { it.id } shouldContainAll listOf(1L, 2L)

                    verify {
                        loginUserContext.getCurrentUser()
                        repository.retrieveCurrentWeekSchedules(
                            user = mockUser,
                            previousSunday = previousSunday,
                            nextSaturday = nextSaturday
                        )
                        mockSchedule1.toDto()
                        mockSchedule2.toDto()
                    }

                }
            }
        }
    }
}



