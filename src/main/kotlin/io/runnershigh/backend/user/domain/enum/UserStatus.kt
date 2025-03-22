package io.runnershigh.backend.user.domain.enum

enum class UserStatus(var value: String) {
    ACTIVE("정상"),
    IDLE("휴면"),
    WITHDRAWAL("탈퇴")
}