package io.runnershigh.backend.training.ui.dto

import io.runnershigh.backend.training.domain.enum.DistanceUnit
import io.runnershigh.backend.training.domain.enum.TargetType
import jakarta.validation.constraints.NotNull
import java.time.LocalTime

data class SaveTrainingPlanItem(

    @field:NotNull
    val itemOrder: Int,

    @field:NotNull
    val targetType: TargetType,

    @field:NotNull
    val targetMinPace: LocalTime,

    @field:NotNull
    val targetMaxPace: LocalTime,

    @field:NotNull
    val targetAvgPace: LocalTime,

    @field:NotNull
    val runningTypeCode: Int,

    val distanceUnit: DistanceUnit?,
    val targetDistance: Double?,
    val targetTime: LocalTime?,

    var estimatedDistance: Double?,
    var estimatedTime: LocalTime?,

    val note: String?,
)
