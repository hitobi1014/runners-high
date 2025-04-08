package io.runnershigh.backend.shared.security

import io.runnershigh.backend.user.domain.enum.UserStatus
import io.runnershigh.backend.user.infrastructure.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(loginId: String): UserDetails {
        userRepository.findByLoginId(loginId)
            ?.takeIf { user -> user.userStatus == UserStatus.ACTIVE }
            ?.let { user ->
                // 설계에서 별도의 권한 없음, 추후 권한 생기면 교체
                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

                return User(
                    user.loginId,
                    user.password,
                    true,
                    true,
                    true,
                    true, // 계정 잠겼는지 확인 (ex. 여러번 잘못된 비밀번호 입력해서 계정 잠기는경우)
                    authorities
                )
            }
            ?: throw IllegalArgumentException("ID 정보가 올바르지 않습니다: $loginId")
    }
}