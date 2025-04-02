package io.runnershigh.backend.training.exception

import org.springframework.http.HttpStatus

enum class TrainingExceptionType(
    val errorCode: String,
    val httpStatus: HttpStatus,
    val message: String,
) {

    CANNOT_REGISTER_PAST_TRAINING(
        errorCode = "TRAINING_001",
        httpStatus = HttpStatus.BAD_REQUEST,
        message = "과거 시간에는 훈련을 등록할 수 없습니다."
    )
}