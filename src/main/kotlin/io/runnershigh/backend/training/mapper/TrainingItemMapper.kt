package io.runnershigh.backend.training.mapper

import io.runnershigh.backend.training.dto.request.SaveTrainingItem
import io.runnershigh.backend.training.entity.TrainingPlanItems

fun SaveTrainingItem.toEntity() = TrainingPlanItems(
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