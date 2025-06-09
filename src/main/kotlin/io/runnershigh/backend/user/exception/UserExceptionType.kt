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
    USER_LOGIN_ID_ALREADY_EXISTS(
        errorCode = "USER_003",
        httpStatus = HttpStatus.CONFLICT,
        message = "이미 존재하는 로그인 아이디입니다.",
    ),
    USER_NICKNAME_ALREADY_EXISTS(
        errorCode = "USER_004",
        httpStatus = HttpStatus.CONFLICT,
        message = "이미 존재하는 닉네임입니다.",
    ),
}