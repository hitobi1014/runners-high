package io.runnershigh.backend.shared.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(e.httpStatus)
            .body(ErrorResponse.of(e))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse.of(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "SYSTEM-999",
                    "서버 내부 오류가 발생했습니다."
                )
            )
    }
}