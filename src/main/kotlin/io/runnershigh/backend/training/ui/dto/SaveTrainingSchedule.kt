package io.runnershigh.backend.training.ui.dto

import io.runnershigh.backend.training.domain.enum.TrainingStatus
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class SaveTrainingSchedule(
    @field:NotBlank(message = "제목은 필수입니다.")
    val title: String,
    val location: String = "",
    @field:NotNull(message = "훈련 예정일자는 필수입니다.")
    @field:FutureOrPresent(message = "훈련 예정일자는 오늘 또는 미래날짜여야합니다.")
    val scheduledDate: LocalDate,
    val description: String = "",
    val status: TrainingStatus = TrainingStatus.PLANNED,
)
