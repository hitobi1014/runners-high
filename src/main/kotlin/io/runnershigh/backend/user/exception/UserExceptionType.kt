package io.runnershigh.backend.user.exception

import org.springframework.http.HttpStatus

enum class UserExceptionType(
    val errorCode: String,
    val httpStatus: HttpStatus,
    val message: String,
) {
    USER_NOT_FOUND(
        errorCode = "USER_001",
        httpStatus = HttpStatus.NOT_FOUND,
        message = "해당 정보로 회원을 찾을 수 없습니다.",
    ),
    USER_INVALID_CREDENTIALS(
        errorCode = "USER_002",
        httpStatus = HttpStatus.UNAUTHORIZED,
        message = "아이디 또는 비밀번호가 다릅니다.",
    ),
}