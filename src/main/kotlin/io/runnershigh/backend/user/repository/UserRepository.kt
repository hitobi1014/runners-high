package io.runnershigh.backend.user.repository

import io.runnershigh.backend.user.entity.UserEntity
import io.runnershigh.backend.user.repository.querydsl.UserRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Int>, UserRepositoryCustom {
    fun findByLoginId(loginId: String): UserEntity?
}