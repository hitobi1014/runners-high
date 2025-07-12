package io.runnershigh.backend.user.entity

enum class UserStatus(val value: String) {
    ACTIVE("정상"),
    IDLE("휴면"),
    WITHDRAWAL("탈퇴")
}