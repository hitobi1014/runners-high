package io.runnershigh.backend.training.exception

import io.runnershigh.backend.shared.exception.BaseException

class TrainingException(
    val exceptionType: TrainingExceptionType,
) : BaseException(
    errorCode = exceptionType.errorCode,
    httpStatus = exceptionType.httpStatus,
    message = exceptionType.message,
)