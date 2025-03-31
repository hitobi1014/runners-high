package io.runnershigh.backend.training.ui.dto

import io.runnershigh.backend.training.domain.enum.TrainingStatus
import java.time.LocalDate

data class SaveTrainingSchedule(
    val title: String,
    val location: String = "",
    val scheduleDate: LocalDate,
    val description: String = "",
    val status: TrainingStatus = TrainingStatus.PLANNED,
)
