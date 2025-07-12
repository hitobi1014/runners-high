package io.runnershigh.backend.init

import io.runnershigh.backend.shared.security.jwt.JwtTokenProvider
import io.runnershigh.backend.training.entity.TrainingPlanItems
import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.training.entity.enum.DistanceUnit
import io.runnershigh.backend.training.entity.enum.TargetType
import io.runnershigh.backend.training.entity.enum.TrainingColor
import io.runnershigh.backend.training.entity.enum.TrainingStatus
import io.runnershigh.backend.training.repository.TrainingPlanItemsRepository
import io.runnershigh.backend.training.repository.TrainingSchedulesRepository
import io.runnershigh.backend.user.entity.UserEntity
import io.runnershigh.backend.user.entity.enum.AgeGroup
import io.runnershigh.backend.user.entity.enum.Gender
import io.runnershigh.backend.user.repository.UserRepository
import mu.KotlinLogging
import net.datafaker.Faker
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Component
@Profile("local")
class DataInitializer(
    private val userRepository: UserRepository,
    private val trainingSchedulesRepository: TrainingSchedulesRepository,
    private val trainingPlanItemsRepository: TrainingPlanItemsRepository,
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
        initTrainingPlanItems()
        initToken()
    }

    // 토큰 발급 후 로깅 출력
    private fun initToken() {
        val user = getUserByLoginId()
        val token = jwtTokenProvider.generateAccessToken(user.id, user.nickname)
        logger.info { "#유저 토큰 정보 test1 : $token" }
    }

    private fun initTrainingSchedules() {
        val user = getUserByLoginId()

        val startInstant = LocalDate.now().atStartOfDay(zoneId).toInstant()
        val endInstant = LocalDate.now().plusDays(7).atStartOfDay(zoneId).toInstant()

        val schedule1 = createSchedule(user, startInstant, endInstant)
        val schedule2 = createSchedule(user, startInstant, endInstant)
        val schedule3 = createSchedule(user, startInstant, endInstant)

        trainingSchedulesRepository.saveAll(listOf(schedule1, schedule2, schedule3))
        trainingSchedulesRepository.flush()
    }

    private fun initTrainingPlanItems() {
        val user = getUserByLoginId()
        // TODO 추후 mock 데이터 리팩토링 -> 자동생성버전, faker 이용버전
        val trainingSchedules = createSchedule2(user, LocalDate.now().plusDays(1))
        trainingSchedulesRepository.save(trainingSchedules)
        trainingSchedulesRepository.flush()

        // TODO 250705) 훈련일정 items가 가지고있을 필요 x, 테스트 후 삭제
//        val planItem1 =
//            createTrainingPlanItems(schedule = trainingSchedules, itemOrder = 1)
//        val planItem2 =
//            createTrainingPlanItems(schedule = trainingSchedules, itemOrder = 2)
//        val planItem3 =
//            createTrainingPlanItems(schedule = trainingSchedules, itemOrder = 3)

//        trainingPlanItemsRepository.saveAll(listOf(planItem1, planItem2, planItem3))
        trainingPlanItemsRepository.flush()
    }

    private fun getUserByLoginId(): UserEntity {
        val user =
            userRepository.findByLoginId("test1") ?: throw IllegalStateException("User not found")
        return user
    }

    private fun initUsers() {
        logger.info { "initUsers 실행" }
        val user1 = createUser(
            loginId = "test1",
            password = passwordEncoder.encode("test1"),
            nickname = "테스트1"
        )
        val user2 = createUser(loginId = "test2", nickname = "테스트2")
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
            color = TrainingColor.entries.toTypedArray().random(),
            description = faker.lorem().characters(20, 100),
            status = TrainingStatus.PLANNED
        )
    }

    private fun createSchedule2(
        user: UserEntity,
        scheduleDate: LocalDate,
    ): TrainingSchedules {
        return TrainingSchedules(
            user = user,
            title = faker.lorem().characters(10, 100),
            location = faker.address().streetAddress(),
            scheduledDate = scheduleDate,
            color = TrainingColor.entries.toTypedArray().random(),
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

    private fun createTrainingPlanItems(
        itemOrder: Int = faker.number().numberBetween(1, 10),
        targetType: TargetType = faker.options().option(TargetType::class.java),
        targetMinPace: Duration = Duration.ofMinutes(faker.number().numberBetween(4, 6).toLong())
            .plusSeconds(faker.number().numberBetween(0, 59).toLong()),
        targetMaxPace: Duration = Duration.ofMinutes(faker.number().numberBetween(5, 7).toLong())
            .plusSeconds(faker.number().numberBetween(0, 59).toLong()),
        targetAvgPace: Duration = Duration.ofMinutes(faker.number().numberBetween(4, 6).toLong())
            .plusSeconds(faker.number().numberBetween(30, 59).toLong()),
        runningTypeCode: Int = faker.number().numberBetween(1, 5),
        distanceUnit: DistanceUnit = faker.options().option(DistanceUnit::class.java),
        targetDistance: Double = faker.number().randomDouble(1, 1, 20),
        targetTime: Duration = Duration.ofMinutes(faker.number().numberBetween(1, 10).toLong()),
        estimatedDistance: Double = faker.number().randomDouble(1, 1, 20),
        estimatedTime: Duration = Duration.ofMinutes(faker.number().numberBetween(1, 10).toLong()),
        note: String = faker.lorem().sentence(faker.number().numberBetween(5, 20)),
    ): TrainingPlanItems {
        return TrainingPlanItems(
            itemOrder = itemOrder,
            targetType = targetType,
            targetMinPace = targetMinPace,
            targetMaxPace = targetMaxPace,
            targetAvgPace = targetAvgPace,
            runningTypeCode = runningTypeCode,
            distanceUnit = distanceUnit,
            targetDistance = targetDistance,
            targetTime = targetTime,
            estimatedDistance = estimatedDistance,
            estimatedTime = estimatedTime,
            note = note
        )
    }


}