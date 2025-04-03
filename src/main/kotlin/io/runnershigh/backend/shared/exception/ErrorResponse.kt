package io.runnershigh.backend.shared.exception

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val code: String,
    val message: String,
) {
    companion object {
        fun of(httpStatus: HttpStatus, errorCode: String, message: String): ErrorResponse {
            return ErrorResponse(
                status = httpStatus.value(),
                error = httpStatus.reasonPhrase,
                code = errorCode,
                message = message,
            )
        }

        fun of(e: BaseException): ErrorResponse {
            return of(e.httpStatus, e.errorCode, e.message)
        }
    }
}
