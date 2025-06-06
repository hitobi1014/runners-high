package io.runnershigh.backend.user.repository.querydsl

interface UserRepositoryCustom {
    fun existsByLoginId(loginId: String): Boolean
    fun existsByNickname(nickname: String): Boolean
}