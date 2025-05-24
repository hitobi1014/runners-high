package io.runnershigh.backend.training.dto.response

import io.runnershigh.backend.training.entity.enum.TrainingStatus
import java.time.LocalDate

data class ReadTrainingSchedule(
    val id: Long,
    val title: String,
    val location: String,
    val scheduledDate: LocalDate,
    val description: String,
    val status: TrainingStatus,
    val color: String,
)
