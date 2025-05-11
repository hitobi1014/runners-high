package io.runnershigh.backend.shared.response

enum class CommonResponseMessage(
    val message: String,
) {
    SUCCESS_GET_DATA("데이터 조회 성공"),
    SUCCESS_UPDATE_DATA("데이터 수정 성공"),
    SUCCESS_DELETE_DATA("데이터 삭제 성공"),

}