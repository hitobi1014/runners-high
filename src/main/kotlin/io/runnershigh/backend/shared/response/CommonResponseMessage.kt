package io.runnershigh.backend.shared.response


/**
 * 공통 응답 메시지를 나타내는 열거형 클래스.
 *
 * 데이터 조회, 수정, 삭제, 생성에 대한 메세지
 *
 * @property message 응답 메시지를 나타내는 문자열.
 */
enum class CommonResponseMessage(
    val message: String,
) {
    SUCCESS_GET_DATA("데이터 조회 성공"),
    SUCCESS_UPDATE_DATA("데이터 수정 성공"),
    SUCCESS_DELETE_DATA("데이터 삭제 성공"),
    SUCCESS_CREATE_DATA("데이터 생성 성공"),

}