package io.runnershigh.backend.training.dto.request

import io.runnershigh.backend.training.entity.enum.DistanceUnit
import io.runnershigh.backend.training.entity.enum.TargetType
import io.runnershigh.backend.training.entity.enum.TrainingColor
import io.runnershigh.backend.training.entity.enum.TrainingStatus
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import java.time.Duration
import java.time.LocalDate

data class SaveTrainingInfo(
    @field:NotBlank(message = "제목은 필수입니다.")
    val title: String,
    val location: String = "",
    @field:NotNull(message = "훈련 예정일자는 필수입니다.")
    @field:FutureOrPresent(message = "훈련 예정일자는 오늘 또는 미래날짜여야합니다.")
    val scheduledDate: LocalDate,
    val description: String = "",
    val status: TrainingStatus = TrainingStatus.PLANNED,
    @field:NotNull(message = "색상은 필수입니다.")
    val color: TrainingColor,
    @field:Valid
    @field:NotEmpty(message = "훈련 그룹은 최소 1개 이상이어야 합니다.")
    val groups: List<SaveTrainingGroup>,
)

data class SaveTrainingGroup(
    @field:Min(value = 1, message = "그룹 순서는 1 이상이어야 합니다.")
    val groupOrder: Int,
    @field:Min(value = 1, message = "반복 횟수는 1 이상이어야 합니다.")
    val repeatCount: Int,
    val description: String = "",
    @field:Valid
    @field:NotEmpty(message = "훈련 아이템은 최소 1개 이상이어야 합니다.")
    val items: List<SaveTrainingItem>,
)

data class SaveTrainingItem(
    @field:Min(value = 1, message = "아이템 순서는 1 이상이어야 합니다.")
    val itemOrder: Int,
    @field:NotNull(message = "목표 타입은 필수입니다.")
    val targetType: TargetType,
    @field:NotNull(message = "목표 최소 페이스는 필수입니다.")
    val targetMinPace: Duration,
    @field:NotNull(message = "목표 최대 페이스는 필수입니다.")
    val targetMaxPace: Duration,
    @field:NotNull(message = "목표 평균 페이스는 필수입니다.")
    val targetAvgPace: Duration,
    @field:NotNull(message = "러닝 타입 코드는 필수입니다.")
    val runningTypeCode: Int,
    val distanceUnit: DistanceUnit,
    @field:DecimalMin(value = "0.0", inclusive = false, message = "목표 거리는 0보다 커야 합니다.")
    val targetDistance: Double,
    val targetTime: Duration,
    @field:DecimalMin(value = "0.0", inclusive = false, message = "예상 거리는 0보다 커야 합니다.")
    val estimatedDistance: Double,
    val estimatedTime: Duration,
    val note: String?,
)
