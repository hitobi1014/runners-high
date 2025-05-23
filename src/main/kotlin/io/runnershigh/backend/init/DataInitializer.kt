package io.runnershigh.backend.init

import io.runnershigh.backend.shared.security.jwt.JwtTokenProvider
import io.runnershigh.backend.training.entity.enum.TrainingStatus
import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.training.repository.TrainingSchedulesRepository
import io.runnershigh.backend.user.domain.enum.AgeGroup
import io.runnershigh.backend.user.domain.enum.Gender
import io.runnershigh.backend.user.infrastructure.entity.UserEntity
import io.runnershigh.backend.user.infrastructure.repository.UserRepository
import mu.KotlinLogging
import net.datafaker.Faker
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

@Component
@Profile("local")
class DataInitializer(
    private val userRepository: UserRepository,
    private val trainingSchedulesRepository: TrainingSchedulesRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,

    @Value("\${app.init.skip-data}")
    private val skipDataInit: Boolean,
) : ApplicationRunner {


    private val faker = Faker(Locale.KOREA)
    private val zoneId = ZoneId.systemDefault()

    private val logger = KotlinLogging.logger {}

    override fun run(args: ApplicationArguments?) {
        if (skipDataInit) {
            logger.info { "Skip Data 활성화, 기초 데이터 생성 pass" }
            return
        }
        initUsers()
        initTrainingSchedules()
        initToken()
    }

    // 토큰 발급 후 로깅 출력
    private fun initToken() {
        val user =
            userRepository.findByLoginId("test1") ?: throw IllegalStateException("User not found")
        val token = jwtTokenProvider.generateToken(user.id, user.nickname)
        logger.info { "#유저 토큰 정보 test1 : $token" }
    }

    private fun initTrainingSchedules() {
        val user =
            userRepository.findByLoginId("test1") ?: throw IllegalStateException("User not found")

        val startInstant = LocalDate.now().atStartOfDay(zoneId).toInstant()
        val endInstant = LocalDate.now().plusDays(7).atStartOfDay(zoneId).toInstant()

        val schedule1 = createSchedule(user, startInstant, endInstant)
        val schedule2 = createSchedule(user, startInstant, endInstant)
        val schedule3 = createSchedule(user, startInstant, endInstant)

        trainingSchedulesRepository.saveAll(listOf(schedule1, schedule2, schedule3))
        trainingSchedulesRepository.flush()
    }

    private fun initUsers() {
        logger.info { "initUsers 실행" }
        val user1 = createUser(loginId = "test1", password = passwordEncoder.encode("test1"))
        val user2 = createUser(loginId = "test2")
        userRepository.saveAll(listOf(user1, user2))
        userRepository.flush()
    }

    private fun createSchedule(
        user: UserEntity,
        startInstant: Instant,
        endInstant: Instant,
    ): TrainingSchedules {
        return TrainingSchedules(
            user = user,
            title = faker.lorem().characters(10, 100),
            location = faker.address().streetAddress(),
            scheduledDate = faker.timeAndDate().between(startInstant, endInstant).atZone(zoneId)
                .toLocalDate(),
            color = faker.color().hex(),
            description = faker.lorem().characters(20, 100),
            status = TrainingStatus.PLANNED
        )
    }

    private fun createUser(
        loginId: String = faker.lorem().characters(30),
        password: String = passwordEncoder.encode(faker.internet().password()),
        nickname: String = faker.funnyName().name(),
        gender: Gender = Gender.MALE,
        profileImage: String = faker.internet().image(640, 480),
        ageGroup: AgeGroup = AgeGroup.TWENTIES,
    ): UserEntity {
        return UserEntity(
            loginId = loginId,
            password = password,
            nickname = nickname,
            gender = gender,
            profileImage = profileImage,
            ageGroup = ageGroup,
        )
    }
}