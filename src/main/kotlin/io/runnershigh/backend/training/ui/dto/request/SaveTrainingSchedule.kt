package io.runnershigh.backend.training.ui.dto.request

import io.runnershigh.backend.training.domain.enum.TrainingStatus
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
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
    @field:NotNull(message = "색상은 필수입니다.")
    @field:Pattern(
        regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "올바른 HEX 색상 코드 형식이어야 합니다."
    )
    val color: String,
)
