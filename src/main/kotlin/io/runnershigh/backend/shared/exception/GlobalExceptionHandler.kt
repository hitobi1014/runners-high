package io.runnershigh.backend.shared.exception

import io.runnershigh.backend.shared.response.ApiResponse
import jakarta.validation.ConstraintViolationException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.method.ParameterValidationResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException

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

    /**
     * RequestBody 검증용
     */
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

    /**
     * 메소드 파라미터 검증용
     */
    @ExceptionHandler(ConstraintViolationException::class, HandlerMethodValidationException::class)
    fun handleMethodValidationException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        val errorMessage = when (e) {
            is ConstraintViolationException -> {
                e.constraintViolations.joinToString(", ") { violation ->
                    val paramName = violation.propertyPath.toString().substringAfterLast(".")
                    "$paramName: ${violation.message}"
                }
            }

            is HandlerMethodValidationException -> {
                e.allErrors.joinToString(", ") { error ->
                    when (error) {
                        is ParameterValidationResult -> {
                            error.resolvableErrors.joinToString(", ") { validationError ->
                                "${error.methodParameter.parameterName ?: "parameter"}: ${validationError.defaultMessage}"
                            }
                        }

                        else -> error.defaultMessage ?: "검증 오류"
                    }
                }
            }

            else -> "알 수 없는 검증 오류"
        }

        logger.warn { "Method parameter validation error: $errorMessage" }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error<Nothing>(
                    status = HttpStatus.BAD_REQUEST,
                    code = ErrorCodes.VALIDATION_ERROR.errorCode,
                    message = "검증 오류 발생: $errorMessage".takeIf { errorMessage.isNotEmpty() }
                        ?: "검증 오류가 발생했습니다."
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