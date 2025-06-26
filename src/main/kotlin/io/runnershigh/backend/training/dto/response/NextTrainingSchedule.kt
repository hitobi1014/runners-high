package io.runnershigh.backend.training.dto.response

import java.time.LocalDateTime
import java.time.LocalTime

class NextTrainingSchedule(
    val scheduleId: Long,
    val title: String,
    val scheduledDate: LocalDateTime,
    val totalDistance: Double,
    val totalTime: LocalTime,
)