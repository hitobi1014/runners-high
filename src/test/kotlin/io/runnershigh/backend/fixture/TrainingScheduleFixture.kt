package io.runnershigh.backend.fixture

import io.mockk.every
import io.mockk.mockk
import io.runnershigh.backend.training.domain.enum.TrainingStatus
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.training.ui.dto.response.ReadTrainingSchedule
import io.runnershigh.backend.user.infrastructure.entity.UserEntity
import java.time.LocalDate

object TrainingScheduleFixture {
    fun createDefault(
        id: Long = 0,
        user: UserEntity = UserFixture.createDefault(),
        title: String = "하프마라톤 대비",
        location: String = "보라매 공원",
        description: String = "보라매 공원 가볍게 러닝",
        scheduledDate: LocalDate = LocalDate.now(),
        status: TrainingStatus = TrainingStatus.PLANNED,
    ): TrainingSchedules {
        return TrainingSchedules(
            id = id,
            user = user,
            title = title,
            location = location,
            description = description,
            scheduledDate = scheduledDate,
            status = status,
        )
    }

    fun createReadTrainingSchedule(
        id: Long = 1L,
        title: String = "훈련1",
        location: String = "여의도 공원",
        scheduledDate: LocalDate = LocalDate.now(),
        description: String = "고구마 캐기",
        status: TrainingStatus = TrainingStatus.PLANNED,
    ): ReadTrainingSchedule {
        return ReadTrainingSchedule(
            id = id,
            title = title,
            location = location,
            scheduledDate = scheduledDate,
            description = description,
            status = status,
        )
    }

    fun createEntityMock(
        id: Long,
        scheduledDate: LocalDate,
        status: TrainingStatus = TrainingStatus.PLANNED,
    ): TrainingSchedules {
        val dto = createReadTrainingSchedule(id, scheduledDate = scheduledDate, status = status)

        return mockk<TrainingSchedules>().apply {
            every { this@apply.id } returns id
            every { this@apply.scheduledDate } returns scheduledDate
            every { this@apply.status } returns status
            every { this@apply.title } returns dto.title
            every { this@apply.location } returns dto.location
            every { this@apply.description } returns dto.description
        }
    }

    fun createMultipleEntityMocks(count: Int): List<TrainingSchedules> {
        return (1..count).map { index ->
            createEntityMock(
                index.toLong(),
                scheduledDate = LocalDate.now().plusDays(index.toLong() - 1)
            )
        }
    }
}