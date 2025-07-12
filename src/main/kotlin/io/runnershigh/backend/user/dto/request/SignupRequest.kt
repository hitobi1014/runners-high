package io.runnershigh.backend.user.dto.request

import io.runnershigh.backend.user.entity.AgeGroup
import io.runnershigh.backend.user.entity.Gender
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignupRequest(
    @field:NotBlank(message = "로그인ID는 필수입니다.")
    @field:Size(min = 6, max = 30, message = "로그인ID는 6자 이상 30자 이하로 입력해야 합니다.")
    val loginId: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @field:Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해야 합니다.")
    val password: String,

    @field:NotBlank(message = "닉네임은 필수입니다.")
    @field:Size(min = 1, max = 20, message = "닉네임은 1자 이상 20자 이하로 입력해야 합니다.")
    val nickname: String,
    val gender: Gender,
    val ageGroup: AgeGroup,
)
