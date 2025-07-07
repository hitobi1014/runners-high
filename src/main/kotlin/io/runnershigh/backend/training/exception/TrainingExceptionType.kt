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
    ),

    CANNOT_FOUND_TRAINING_SCHEDULE(
        errorCode = "TRAINING_003",
        httpStatus = HttpStatus.NOT_FOUND,
        message = "해당 정보로 등록된 훈련 일정을 찾을 수 없습니다."
    ),

    INVALID_GROUP_ORDER(
        errorCode = "TRAINING_004",
        httpStatus = HttpStatus.BAD_REQUEST,
        message = "그룹 순서는 1부터 연속적이어야 합니다."
    ),

    INVALID_ITEM_ORDER(
        errorCode = "TRAINING_005",
        httpStatus = HttpStatus.BAD_REQUEST,
        message = "아이템 순서는 1부터 연속적이어야 합니다."
    ),

    INVALID_PACE_RANGE(
        errorCode = "TRAINING_006",
        httpStatus = HttpStatus.BAD_REQUEST,
        message = "페이스 범위가 올바르지 않습니다. (최소 ≤ 평균 ≤ 최대)"
    ),

    TRAINING_DISTANCE_LIMIT_EXCEEDED(
        errorCode = "TRAINING_007",
        httpStatus = HttpStatus.BAD_REQUEST,
        message = "총 훈련 거리는 100km를 초과할 수 없습니다."
    ),


}