package io.runnershigh.backend.training.domain.mapper

import io.runnershigh.backend.training.infrastructure.entity.TrainingPlanItems
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.training.ui.dto.request.SaveTrainingPlanItem

/**
 * Converts this [SaveTrainingPlanItem] DTO to a [TrainingPlanItems] entity.
 *
 * The function maps corresponding properties from the DTO to the entity and associates the
 * result with the provided [TrainingSchedules]. This includes transferring values such as item order,
 * pace targets, running type code, distance unit, target and estimated values for distance and time,
 * as well as any note.
 *
 * @param schedule the training schedule to be associated with the training plan item.
 * @return a new [TrainingPlanItems] entity with values derived from this DTO.
 */
fun SaveTrainingPlanItem.toEntity(schedule: TrainingSchedules) = TrainingPlanItems(
    schedule = schedule,
    itemOrder = this.itemOrder,
    targetType = this.targetType,
    targetMinPace = this.targetMinPace,
    targetMaxPace = this.targetMaxPace,
    targetAvgPace = this.targetAvgPace,
    runningTypeCode = this.runningTypeCode,
    distanceUnit = this.distanceUnit,
    targetDistance = this.targetDistance,
    targetTime = this.targetTime,
    estimatedDistance = this.estimatedDistance,
    estimatedTime = this.estimatedTime,
    note = this.note
)