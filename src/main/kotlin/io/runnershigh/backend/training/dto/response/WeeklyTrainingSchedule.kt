package io.runnershigh.backend.training.dto.response

import io.runnershigh.backend.training.entity.TrainingColor
import io.runnershigh.backend.training.entity.TrainingStatus
import java.time.LocalDateTime

data class WeeklyTrainingSchedule(
    val scheduleId: Long,
    val title: String,
    val dayOfWeek: String,
    val distance: Double,
    val status: TrainingStatus,
    val scheduledDateTime: LocalDateTime,
    val trainingColor: TrainingColor,
)