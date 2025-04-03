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
    ),

    CANNOT_REGISTER_TRAINING_BEYOND_ONE_YEAR(
        errorCode = "TRAINING_002",
        httpStatus = HttpStatus.BAD_REQUEST,
        message = "최대 1년까지만 훈련 일정을 등록할 수 있습니다."
    )
}