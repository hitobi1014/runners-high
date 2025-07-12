package io.runnershigh.backend.user.entity

enum class Gender(val description: String) {
    MALE("남성"),
    FEMALE("여성"),
    PREFER_NOT_TO_SAY("응답하지 않음");

    companion object {
        fun fromString(value: String): Gender {
            return Gender.entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("선택한 Gender가 없습니다. $value")
        }
    }
}