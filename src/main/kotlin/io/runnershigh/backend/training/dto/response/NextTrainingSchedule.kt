package io.runnershigh.backend.training.dto.response

import java.time.Duration
import java.time.LocalDateTime

data class NextTrainingSchedule(
    val scheduleId: Long,
    val title: String,
    val scheduledDateTime: LocalDateTime,
    val totalDistance: Double,
    val totalTime: Duration,
)