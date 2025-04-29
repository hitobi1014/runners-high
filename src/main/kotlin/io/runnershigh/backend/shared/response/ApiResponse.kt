package io.runnershigh.backend.shared.response

import io.runnershigh.backend.shared.exception.BaseException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

class ApiResponse<T> private constructor(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val success: Boolean,
    val message: String?,
    val data: T?,
    val error: ErrorInfo?,
) {
    companion object {
        // 성공 (데이터 o)
        fun <T> success(
            data: T,
            message: String? = null,
            status: HttpStatus = HttpStatus.OK,
        ): ApiResponse<T> {
            return ApiResponse(
                status = status.value(),
                success = true,
                message = message,
                data = data,
                error = null
            )
        }

        // 성공 (데이터 x)
        fun success(
            message: String? = "성공",
            status: HttpStatus = HttpStatus.OK,
        ): ApiResponse<Unit> {
            return ApiResponse(
                status = status.value(),
                success = true,
                message = message,
                data = Unit,
                error = null
            )
        }

        // 실패 응답 (공통 에러처리 클래스, BaseException)
        fun <T> error(e: BaseException): ApiResponse<T> {
            return ApiResponse(
                status = e.httpStatus.value(),
                success = false,
                message = e.message,
                data = null,
                error = ErrorInfo(
                    code = e.errorCode,
                    message = e.message,
                )
            )
        }

        // 실패 응답 (일반 예외)
        fun <T> error(
            status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
            code: String = "SYSTEM-999",
            message: String = "서버 내부 오류가 발생했습니다.",
        ): ApiResponse<T> {
            return ApiResponse(
                status = status.value(),
                success = false,
                message = message,
                data = null,
                error = ErrorInfo(
                    code = code,
                    message = message,
                )
            )
        }

        
        /**
         * ApiResponse를 ResponseEntity 형태로 반환합니다.
         *
         * @param response ApiResponse 객체
         * @return ResponseEntity 형태로 래핑된 ApiResponse
         */
        fun <T> of(response: ApiResponse<T>): ResponseEntity<ApiResponse<T>> {
            return ResponseEntity.status(response.status).body(response)
        }
    }
}

data class ErrorInfo(
    val code: String,
    val message: String,
)