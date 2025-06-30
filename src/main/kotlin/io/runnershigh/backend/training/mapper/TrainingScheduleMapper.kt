package io.runnershigh.backend.training.mapper

import io.runnershigh.backend.training.dto.request.SaveTrainingSchedule
import io.runnershigh.backend.training.dto.response.ReadTrainingSchedule
import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.user.entity.UserEntity

// request
fun SaveTrainingSchedule.toEntity(user: UserEntity) = TrainingSchedules(
    user = user,
    title = this.title,
    location = this.location,
    scheduledDate = this.scheduledDate,
    description = this.description,
    status = this.status,
    color = this.color
)

// response
fun TrainingSchedules.toDto() = ReadTrainingSchedule(
    id = this.id,
    title = this.title,
    location = this.location,
    scheduledDate = this.scheduledDate,
    description = this.description,
    status = this.status,
    color = this.color
)