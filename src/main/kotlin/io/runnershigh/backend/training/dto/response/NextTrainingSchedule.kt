package io.runnershigh.backend.training.dto.response

import java.time.Duration
import java.time.LocalDate

data class NextTrainingSchedule(
    val scheduleId: Long,
    val title: String,
    val scheduledDate: LocalDate,
    val totalDistance: Double,
    val totalTime: Duration,
)