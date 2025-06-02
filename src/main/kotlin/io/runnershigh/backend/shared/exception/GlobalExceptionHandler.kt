package io.runnershigh.backend.shared.exception

import io.runnershigh.backend.shared.response.ApiResponse
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn { "BaseException: ${e.message}" }
        return ResponseEntity
            .status(e.httpStatus)
            .body(ApiResponse.error(e))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val errorMessage = e.bindingResult.fieldErrors.joinToString(", ") {
            "${it.field}: ${it.defaultMessage}"
        }

        logger.warn { "Validation error: ${e.message}" }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error<Nothing>(
                    status = HttpStatus.BAD_REQUEST,
                    code = ErrorCodes.VALIDATION_ERROR.errorCode,
                    message = errorMessage
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        logger.error(e) { "Unhandled exception: ${e.message}" }
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "SYSTEM-999",
                    "서버 내부 오류가 발생했습니다."
                )
            )
    }
}