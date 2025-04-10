package io.runnershigh.backend.user.exception

import io.runnershigh.backend.shared.exception.BaseException

class UserException(val exceptionType: UserExceptionType) : BaseException(
    errorCode = exceptionType.errorCode,
    httpStatus = exceptionType.httpStatus,
    message = exceptionType.message
)