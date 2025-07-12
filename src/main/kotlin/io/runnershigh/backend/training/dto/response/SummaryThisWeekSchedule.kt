package io.runnershigh.backend.training.dto.response

import java.time.Duration

data class SummaryThisWeekSchedule(
    val scheduleCount: Int,
    val totalDistance: Double,
    val totalTime: Duration,
)
