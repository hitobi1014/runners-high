package io.runnershigh.backend.training.mapper

import io.runnershigh.backend.training.dto.request.SaveTrainingGroup
import io.runnershigh.backend.training.entity.TrainingPlanGroups
import io.runnershigh.backend.training.entity.TrainingSchedules

fun SaveTrainingGroup.toEntity(schedule: TrainingSchedules) = TrainingPlanGroups(
    schedule = schedule,
    groupOrder = this.groupOrder,
    repeatCount = this.repeatCount,
    description = this.description,
)