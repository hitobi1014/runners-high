package io.runnershigh.backend.training.repository.querydsl

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.runnershigh.backend.fixture.training.TrainingScheduleFixture
import io.runnershigh.backend.fixture.UserFixture
import io.runnershigh.backend.shared.annotation.QuerydslTest
import io.runnershigh.backend.training.repository.TrainingSchedulesRepository
import io.runnershigh.backend.user.entity.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.LocalDate

@QuerydslTest
class TrainingScheduleQuerydslTest : BehaviorSpec() {

    @Autowired
    private lateinit var trainingScheduleRepository: TrainingSchedulesRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    private fun initData() {
        val user = UserFixture.createDefault(loginId = "test1")
        testEntityManager.persist(user)

        val trainingSchedules = TrainingScheduleFixture.createDefault(
            scheduledDate = LocalDate.now().plusDays(1),
            title = "보라매런",
            user = user
        )

        testEntityManager.persist(trainingSchedules)
        testEntityManager.flush()
        testEntityManager.clear()
    }

    init {
        beforeSpec {
            initData()
        }

        Given("등록된 유저가 훈련 일정이 있을 때") {

            val user = testEntityManager.entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.loginId = :loginId", UserEntity::class.java
            )
                .setParameter("loginId", "test1")
                .singleResult

            When("해당 사용자의 훈련 일정을 조회하면") {
                val schedules = trainingScheduleRepository.retrieveTrainingSchedules(user)

                Then("해당 사용자의 훈련 일정이 조회된다.") {
                    schedules shouldHaveSize 1
                    schedules.first().user shouldBe user
                }
            }

            When("다음 훈련 일정을 조회 하면") {
                val nextUpcomingSchedule =
                    trainingScheduleRepository.retrieveNextUpcomingSchedule(user)

                Then("훈련 일정이 조회된다.") {
                    nextUpcomingSchedule?.title shouldBe "보라매런"
                }
            }
        }
    }
}