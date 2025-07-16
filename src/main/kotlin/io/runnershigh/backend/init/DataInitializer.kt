package io.runnershigh.backend.init

import io.runnershigh.backend.shared.security.jwt.JwtTokenProvider
import io.runnershigh.backend.training.entity.*
import io.runnershigh.backend.training.repository.TrainingPlanItemsRepository
import io.runnershigh.backend.training.repository.TrainingPlanGroupRepository
import io.runnershigh.backend.training.repository.TrainingSchedulesRepository
import io.runnershigh.backend.user.entity.AgeGroup
import io.runnershigh.backend.user.entity.Gender
import io.runnershigh.backend.user.entity.UserEntity
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
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Component
@Profile("local")
class DataInitializer(
    private val userRepository: UserRepository,
    private val trainingSchedulesRepository: TrainingSchedulesRepository,
    private val trainingPlanGroupRepository: TrainingPlanGroupRepository,
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
        val trainingSchedules = createSchedule2(user, LocalDateTime.now().plusDays(1))
        trainingSchedulesRepository.save(trainingSchedules)

        trainingSchedulesRepository.flush()
        trainingPlanItemsRepository.flush()
    }

    private fun getUserByLoginId(): UserEntity {
        val user =
            userRepository.findByLoginId("test1") ?: throw IllegalStateException("User not found")
        return user
    }

    private fun initUsers() {
        logger.info { "initUsers 실행" }

        // test1, test2, test3 사용자 생성
        val user1 = createUser(
            loginId = "test1",
            password = passwordEncoder.encode("test1"),
            nickname = faker.name().fullName()
        )
        val user2 = createUser(
            loginId = "test2",
            password = passwordEncoder.encode("test2"),
            nickname = faker.name().fullName()
        )
        val user3 = createUser(
            loginId = "test3",
            password = passwordEncoder.encode("test3"),
            nickname = faker.name().fullName()
        )

        userRepository.saveAll(listOf(user1, user2, user3))
        userRepository.flush()

        // 각 유저당 50개씩 훈련 데이터 생성
        listOf(user1, user2, user3).forEach { user ->
            createTrainingDataForUser(user, 50)
        }
    }

    private fun createTrainingDataForUser(user: UserEntity, count: Int) {
        repeat(count) {
            // 현재일부터 1주일 후까지 랜덤 날짜 생성
            val startDate = LocalDateTime.now()
            val randomDate = startDate.plusDays(faker.number().numberBetween(0, 8).toLong())

            // TrainingSchedule 생성
            val schedule = TrainingSchedules(
                user = user,
                title = generateRandomScheduleTitle(),
                location = generateRandomLocation(),
                scheduledDateTime = randomDate,
                color = TrainingColor.entries.toTypedArray().random(),
                description = faker.lorem().sentence(),
                status = TrainingStatus.PLANNED
            )
            val savedSchedule = trainingSchedulesRepository.save(schedule)

            // TrainingPlanGroups 생성 (1-3개 랜덤)
            val groupCount = faker.number().numberBetween(1, 4)
            repeat(groupCount) { groupIndex ->
                val group = TrainingPlanGroups(
                    schedule = savedSchedule,
                    groupOrder = groupIndex + 1,
                    repeatCount = faker.number().numberBetween(1, 4),
                    description = faker.lorem().sentence()
                )
                val savedGroup = trainingPlanGroupRepository.save(group)

                // 각 그룹마다 TrainingPlanItems 생성 (1-3개 랜덤)
                val itemCount = faker.number().numberBetween(1, 4)
                repeat(itemCount) { itemIndex ->
                    val planItem = TrainingPlanItems(
                        group = savedGroup,
                        itemOrder = itemIndex + 1,
                        targetType = TargetType.entries.random(),
                        targetMinPace = generateRandomDuration(4, 6),
                        targetMaxPace = generateRandomDuration(5, 7),
                        targetAvgPace = generateRandomDuration(4, 6),
                        runningTypeCode = faker.number().numberBetween(1, 6),
                        distanceUnit = DistanceUnit.entries.random(),
                        targetDistance = faker.number().randomDouble(1, 1, 21),
                        targetTime = generateRandomDuration(10, 120),
                        estimatedDistance = faker.number().randomDouble(1, 1, 21),
                        estimatedTime = generateRandomDuration(10, 120),
                        note = faker.lorem().sentence()
                    )
                    trainingPlanItemsRepository.save(planItem)
                }
            }
        }
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
            scheduledDateTime = faker.timeAndDate().between(startInstant, endInstant).atZone(zoneId)
                .toLocalDateTime(),
            color = TrainingColor.entries.toTypedArray().random(),
            description = faker.lorem().characters(20, 100),
            status = TrainingStatus.PLANNED
        )
    }

    private fun createSchedule2(
        user: UserEntity,
        scheduleDate: LocalDateTime,
    ): TrainingSchedules {
        return TrainingSchedules(
            user = user,
            title = faker.lorem().characters(10, 100),
            location = faker.address().streetAddress(),
            scheduledDateTime = scheduleDate,
            color = TrainingColor.entries.toTypedArray().random(),
            description = faker.lorem().characters(20, 100),
            status = TrainingStatus.PLANNED
        )
    }

    private fun createUser(
        loginId: String = faker.lorem().characters(30),
        password: String = passwordEncoder.encode(faker.internet().password()),
        nickname: String = faker.funnyName().name(),
        gender: Gender = Gender.entries.random(),
        profileImage: String = faker.internet().image(640, 480),
        ageGroup: AgeGroup = AgeGroup.entries.random(),
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

    private fun generateRandomScheduleTitle(): String {
        val titles = listOf(
            "템포런 훈련", "LSD 장거리", "인터벌 러닝", "페이스 러닝", "힐 러닝",
            "조깅", "쿨다운", "워밍업", "스피드 워크", "크로스 트레이닝"
        )
        return titles.random()
    }

    private fun generateRandomLocation(): String {
        val locations = listOf(
            "올림픽공원", "한강공원", "남산공원", "여의도공원", "선유도공원",
            "월드컵공원", "보라매공원", "중앙공원", "탄천", "청계천"
        )
        return locations.random()
    }

    private fun generateRandomDuration(minMinutes: Int, maxMinutes: Int): Duration {
        val minutes = faker.number().numberBetween(minMinutes, maxMinutes).toLong()
        val seconds = faker.number().numberBetween(0, 60).toLong()
        return Duration.ofMinutes(minutes).plusSeconds(seconds)
    }
}