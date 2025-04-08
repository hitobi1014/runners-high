package io.runnershigh.backend.user.util

import io.runnershigh.backend.user.infrastructure.entity.UserEntity
import io.runnershigh.backend.user.infrastructure.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class LoginUserContext(
    private val userRepository: UserRepository,
) {
    private fun getCurrentContextUserId(): Int {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication.name?.toInt() ?: throw IllegalStateException("User not found")
    }

    fun getCurrentUser(): UserEntity {
        val userId = getCurrentContextUserId()
        return userRepository.findById(userId)
            .orElseThrow { IllegalStateException("User not found") } // TODO 예외 변경하기
    }
}