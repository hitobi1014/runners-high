package io.runnershigh.backend.user.repository

import io.runnershigh.backend.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Int> {
    fun findByLoginId(loginId: String): UserEntity?
}