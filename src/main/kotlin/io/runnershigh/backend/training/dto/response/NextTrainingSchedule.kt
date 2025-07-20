package io.runnershigh.backend.training.dto.response

import io.runnershigh.backend.training.entity.TrainingColor
import java.time.Duration
import java.time.LocalDateTime

data class NextTrainingSchedule(
    val scheduleId: Long,
    val title: String,
    val scheduledDateTime: LocalDateTime,
    val estimatedDistance: Double,
    val estimatedTime: Duration,
    val trainingColor: TrainingColor,
)