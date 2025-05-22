package io.runnershigh.backend.fixture

import io.runnershigh.backend.user.domain.enum.AgeGroup
import io.runnershigh.backend.user.domain.enum.Gender
import io.runnershigh.backend.user.domain.enum.UserStatus
import io.runnershigh.backend.user.infrastructure.entity.UserEntity
import net.datafaker.Faker
import java.util.*

object UserFixture {
    private val faker = Faker(Locale.KOREA)

    fun createDefault(
        id: Int = 0,
        loginId: String = faker.internet().uuid(),
        password: String = faker.internet().password(),
        nickname: String = faker.funnyName().name(),
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