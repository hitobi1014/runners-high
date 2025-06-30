package io.runnershigh.backend.training.dto.response

import java.time.LocalDate
import java.time.LocalTime

class NextTrainingSchedule(
    val scheduleId: Long,
    val title: String,
    val scheduledDate: LocalDate,
    val totalDistance: Double,
    val totalTime: LocalTime,
)