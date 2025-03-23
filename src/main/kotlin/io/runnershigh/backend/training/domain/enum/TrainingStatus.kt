package io.runnershigh.backend.training.domain.enum

enum class TrainingStatus(val value: String) {
    PLANNED("계획완료"),
    COMPLETED("일정완료"),
    CANCELED("계획취소")
}