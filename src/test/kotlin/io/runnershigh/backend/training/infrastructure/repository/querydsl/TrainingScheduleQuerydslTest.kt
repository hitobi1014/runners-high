package io.runnershigh.backend.training.infrastructure.repository.querydsl

import io.runnershigh.backend.fixture.TrainingScheduleFixture
import io.runnershigh.backend.fixture.UserFixture
import io.runnershigh.backend.shared.annotation.QuerydslTest
import io.runnershigh.backend.user.infrastructure.entity.UserEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@QuerydslTest
@Import(TrainingScheduleQuerydsl::class)
class TrainingScheduleQuerydslTest {

    @Autowired
    private lateinit var trainingScheduleQuerydsl: TrainingScheduleQuerydsl

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @BeforeEach
    fun setUp() {
        val user = UserFixture.createDefault()
        entityManager.persist(user)
        val trainingSchedules = TrainingScheduleFixture.createDefault(
            scheduledDate = LocalDate.now().plusDays(1),
            user = user
        )
        entityManager.persist(trainingSchedules)
        entityManager.flush()
        entityManager.clear()
    }

    @Test
    @Transactional
    @DisplayName("저장된 훈련 일정 정상 조회")
    fun getTrainingSchedules() {
        //given
        val user = entityManager.createQuery(
            "SELECT u FROM UserEntity u WHERE u.loginId = :loginId", UserEntity::class.java
        )
            .setParameter("loginId", "test1234")
            .singleResult

        //when
        val schedules = trainingScheduleQuerydsl.findByUser(user)

        //then
        assertEquals(schedules.size, 1)
        assertEquals(schedules.first().user, user)
    }
}