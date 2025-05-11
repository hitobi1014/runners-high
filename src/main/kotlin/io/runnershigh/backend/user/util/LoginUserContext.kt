package io.runnershigh.backend.user.util

import io.runnershigh.backend.user.exception.UserException
import io.runnershigh.backend.user.exception.UserExceptionType
import io.runnershigh.backend.user.infrastructure.entity.UserEntity
import io.runnershigh.backend.user.infrastructure.repository.UserRepository
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class LoginUserContext(
    private val userRepository: UserRepository,
) {
    private val logger = KotlinLogging.logger {}

    private fun getCurrentContextUserId(): Int {
        val authentication = SecurityContextHolder.getContext().authentication
        try {
            return authentication.name?.toInt() ?: throw IllegalStateException("User not found")
        } catch (e: Exception) {
            e.printStackTrace()
            throw UserException(UserExceptionType.USER_NOT_FOUND)
        }
    }

    fun getCurrentUser(): UserEntity {
        val userId = getCurrentContextUserId()
        return userRepository.findByIdOrNull(userId)
            ?: throw UserException(UserExceptionType.USER_NOT_FOUND)
    }
}