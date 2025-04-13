package io.runnershigh.backend.training.domain.mapper

import io.runnershigh.backend.training.infrastructure.entity.TrainingPlanGroups
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.training.ui.dto.request.SaveTrainingPlanGroup

/**
 * Converts a [SaveTrainingPlanGroup] into a [TrainingPlanGroups] entity.
 *
 * Maps the DTO properties (`groupOrder`, `repeatCount`, and `description`) to a new entity,
 * and associates it with the provided [TrainingSchedules].
 *
 * @param schedule The training schedule to link with the group.
 * @return A new [TrainingPlanGroups] instance reflecting the mapped properties and the specified schedule.
 */
fun SaveTrainingPlanGroup.toEntity(schedule: TrainingSchedules) = TrainingPlanGroups(
    schedule = schedule,
    groupOrder = this.groupOrder,
    repeatCount = this.repeatCount,
    description = this.description,
)