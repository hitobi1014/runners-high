package io.runnershigh.backend.user.repository.query

import io.runnershigh.backend.user.infrastructure.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserQueryRepository : JpaRepository<UserEntity, Int> {
    fun findByLoginId(loginId: String): UserEntity?
}