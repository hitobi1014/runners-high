package io.runnershigh.backend.training.mapper

import io.runnershigh.backend.training.entity.TrainingPlanGroups
import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.training.dto.request.SaveTrainingPlanGroup

fun SaveTrainingPlanGroup.toEntity(schedule: TrainingSchedules) = TrainingPlanGroups(
    schedule = schedule,
    groupOrder = this.groupOrder,
    repeatCount = this.repeatCount,
    description = this.description,
)