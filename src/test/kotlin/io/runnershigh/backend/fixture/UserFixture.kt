package io.runnershigh.backend.fixture

import io.runnershigh.backend.user.domain.enum.AgeGroup
import io.runnershigh.backend.user.domain.enum.Gender
import io.runnershigh.backend.user.domain.enum.UserStatus
import io.runnershigh.backend.user.infrastructure.entity.UserEntity

object UserFixture {
    fun createDefault(
        id: Int = 0,
        loginId: String = "test1234",
        password: String = "pw1234",
        nickname: String = "테스트유저",
        gender: Gender = Gender.MALE,
        profileImage: String = "",
        ageGroup: AgeGroup = AgeGroup.TWENTIES,
        userStatus: UserStatus = UserStatus.ACTIVE,
    ): UserEntity {
        return UserEntity(
            id = id,
            loginId = loginId,
            password = password,
            nickname = nickname,
            gender = gender,
            profileImage = profileImage,
            ageGroup = ageGroup,
            userStatus = userStatus,
        )
    }
}