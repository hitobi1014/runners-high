package io.runnershigh.backend.fixture

import io.runnershigh.backend.user.entity.UserEntity
import io.runnershigh.backend.user.entity.AgeGroup
import io.runnershigh.backend.user.entity.Gender
import io.runnershigh.backend.user.entity.UserStatus
import net.datafaker.Faker
import java.util.*

object UserFixture {
    private val faker = Faker(Locale.KOREA)

    fun createDefault(
        id: Int = 0,
        loginId: String = faker.internet().uuid().take(30),
        password: String = faker.internet().password(),
        nickname: String = faker.funnyName().name().take(20),
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