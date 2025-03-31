package io.runnershigh.backend.training.domain.mapper

import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.training.ui.dto.SaveTrainingSchedule
import io.runnershigh.backend.user.infrastructure.entity.UserEntity

// 훈련 일정 mapper
fun SaveTrainingSchedule.toEntity(user: UserEntity): TrainingSchedules {
    return TrainingSchedules(
        user = user,
        title = this.title,
        location = this.location,
        scheduledDate = this.scheduleDate,
        description = this.description,
        status = this.status
    )
}