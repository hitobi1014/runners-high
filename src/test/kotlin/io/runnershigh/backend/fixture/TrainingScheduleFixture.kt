package io.runnershigh.backend.fixture

import io.runnershigh.backend.training.domain.enum.TrainingStatus
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
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
}