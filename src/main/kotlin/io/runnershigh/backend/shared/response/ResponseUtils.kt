package io.runnershigh.backend.shared.response

import io.runnershigh.backend.shared.exception.BaseException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object ResponseUtils {


    /**
     * 요청이 성공했음을 나타내는 응답을 생성합니다.
     *
     * @param T 데이터의 유형
     * @param data 응답 본문에 포함될 데이터
     * @param message 응답에 대한 메시지 (기본값: null)
     * @param status HTTP 응답 상태 코드 (기본값: HttpStatus.OK)
     * @return 성공 상태의 ResponseEntity 객체
     */
    fun <T> success(
        data: T,
        message: String? = null,
        status: HttpStatus = HttpStatus.OK,
    ): ResponseEntity<ApiResponse<T>> {
        return ResponseEntity.status(status).body(ApiResponse.success(data, message, status))
    }


    /**
     * 요청이 성공했음을 나타내는 응답을 생성합니다.
     *
     * @param message 응답에 대한 메시지 (기본값: "성공")
     * @param status HTTP 응답 상태 코드 (기본값: HttpStatus.OK)
     * @return 성공 상태의 ResponseEntity 객체
     */
    fun success(
        message: String? = "성공",
        status: HttpStatus = HttpStatus.OK,
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(status).body(ApiResponse.success(message, status))
    }


    /**
     * 작성 요청에 따라 성공 상태의 응답을 생성합니다.
     *
     * @param T 데이터의 유형
     * @param data 응답 본문에 포함될 데이터
     * @param message 응답에 대한 메시지 (기본값: null)
     * @param status HTTP 응답 상태 코드 (기본값: HttpStatus.OK)
     * @return 성공 상태의 ResponseEntity 객체
     */
    fun <T> created(data: T, message: String? = "생성 성공"): ResponseEntity<ApiResponse<T>> {
        return success(data, message, HttpStatus.CREATED)
    }


    /**
     * 요청이 실패했음을 나타내는 응답을 생성합니다.
     *
     * @param T 데이터의 유형
     * @param status HTTP 응답 상태 코드 (기본값: HttpStatus.INTERNAL_SERVER_ERROR)
     * @param code 오류 코드 (기본값: "SYSTEM-999")
     * @param message 오류 메시지 (기본값: "서버 내부 오류가 발생했습니다.")
     * @return 실패 상태의 ResponseEntity 객체
     */
    fun <T> error(
        status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        code: String = "SYSTEM-999",
        message: String = "서버 내부 오류가 발생했습니다.",
    ): ResponseEntity<ApiResponse<T>> {
        return ResponseEntity.status(status)
            .body(ApiResponse.error(status, code, message))
    }


    /**
     * BaseException을 기반으로 요청이 실패했음을 나타내는 응답을 생성합니다.
     *
     * @param e BaseException 객체
     * @return 실패 상태의 ResponseEntity 객체
     */
    fun <T> error(e: BaseException): ResponseEntity<ApiResponse<T>> {
        return ResponseEntity.status(e.httpStatus)
            .body(ApiResponse.error(e))
    }
}