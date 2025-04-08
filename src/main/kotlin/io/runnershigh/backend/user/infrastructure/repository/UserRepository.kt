package io.runnershigh.backend.user.infrastructure.repository

import io.runnershigh.backend.user.infrastructure.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Int> {
    fun findByLoginId(loginId: String): UserEntity?
}