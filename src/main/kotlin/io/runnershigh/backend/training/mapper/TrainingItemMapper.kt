package io.runnershigh.backend.training.mapper

import io.runnershigh.backend.training.entity.TrainingPlanItems
import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.training.dto.request.SaveTrainingPlanItem

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