package io.runnershigh.backend.training.dto.response

import io.runnershigh.backend.training.entity.TrainingColor
import io.runnershigh.backend.training.entity.TrainingStatus
import java.time.Duration
import java.time.LocalDateTime

data class WeeklyTrainingSchedule(
    val scheduleId: Long,
    val title: String,
    val scheduledDateTime: LocalDateTime,
    val dayOfWeek: String,
    val estimatedDistance: Double,
    val estimatedTime: Duration,
    val trainingColor: TrainingColor,
    val status: TrainingStatus,
)