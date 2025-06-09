package io.runnershigh.backend.user.service

import io.runnershigh.backend.user.dto.request.LoginRequest
import io.runnershigh.backend.user.dto.request.SignupRequest
import io.runnershigh.backend.user.dto.response.LoginResponse

interface UserService {
    /**
     * 사용자 로그인을 처리합니다.
     *
     * @param request 로그인에 필요한 사용자 정보를 담은 요청 객체
     * @return 로그인 성공 시 사용자 정보와 토큰을 포함한 응답
     */
    fun login(request: LoginRequest): LoginResponse

    /**
     * 새로운 사용자를 등록합니다.
     *
     * @param request 회원가입에 필요한 사용자 정보를 담은 요청 객체
     * @return 회원가입 성공 시 생성된 사용자의 식별자
     */
    fun signup(request: SignupRequest): String

    /**
     * 로그인 아이디의 사용 가능 여부를 확인합니다.
     *
     * @param loginId 확인할 로그인 아이디
     * @return 사용 가능하면 true, 이미 사용 중이면 false
     */
    fun isLoginIdAvailable(loginId: String): Boolean

    /**
     * 닉네임의 사용 가능 여부를 확인합니다.
     *
     * @param nickname 확인할 닉네임
     * @return 사용 가능하면 true, 이미 사용 중이면 false
     */
    fun isNicknameAvailable(nickname: String): Boolean
}