package io.runnershigh.backend.training.domain.mapper

import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.training.ui.dto.request.SaveTrainingSchedule
import io.runnershigh.backend.user.infrastructure.entity.UserEntity

// Training schedule mapper
fun SaveTrainingSchedule.toEntity(user: UserEntity) = TrainingSchedules(
    user = user,
    title = this.title,
    location = this.location,
    scheduledDate = this.scheduledDate,
    description = this.description,
    status = this.status
)