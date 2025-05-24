package io.runnershigh.backend.shared.security

/*
* 추후 사용여부 확인 후 제거
*
* 현재 로그인 프로세스가 회원 로그인 > api 호출 > 비즈니스 로직 (DB 조회) > JWT 생성
* 따로 JWT 토큰에서 user name을 꺼내서 토큰 인증시 db조회 하지 않음, 토큰 유효성만 검증 하는중
* 인증 요청마다 캐싱이나 DB 조회해서 필요할 경우 사용하도록 함
* */

//@Service
@Deprecated(message = "사용여부 확인 후 제거")
class CustomUserDetailsService(
//    private val userRepository: UserRepository,
)
//    : UserDetailsService
{

//    /**
//     * 회원이 로그인할때만 메소드 호출 후
//     */
//    @Transactional(readOnly = true)
//    override fun loadUserByUsername(id: String): UserDetails {
//        userRepository.findByIdOrNull(id.toInt())
//            ?.takeIf { user -> user.userStatus == UserStatus.ACTIVE }
//            ?.let { user ->
//                // 설계에서 별도의 권한 없음, 추후 권한 생기면 교체
//                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
//
//                return User(
//                    user.loginId,
//                    user.password,
//                    true,
//                    true,
//                    true,
//                    true, // 계정 잠겼는지 확인 (ex. 여러번 잘못된 비밀번호 입력해서 계정 잠기는경우)
//                    authorities
//                )
//            }
//            ?: throw IllegalArgumentException("ID 정보가 올바르지 않습니다: $id")
//    }
}