package io.runnershigh.backend.user.domain.enum

enum class Gender {
    MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY;

    fun toDisplayName(): String {
        return when (this) {
            MALE -> "남성"
            FEMALE -> "여성"
            OTHER -> "기타"
            PREFER_NOT_TO_SAY -> "응답하지 않음"
        }
    }
}