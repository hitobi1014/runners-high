package io.runnershigh.backend.training.domain.mapper

import io.runnershigh.backend.training.infrastructure.entity.TrainingPlanGroups
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.training.ui.dto.request.SaveTrainingPlanGroup

fun SaveTrainingPlanGroup.toEntity(schedule: TrainingSchedules) = TrainingPlanGroups(
    schedule = schedule,
    groupOrder = this.groupOrder,
    repeatCount = this.repeatCount,
    description = this.description,
)