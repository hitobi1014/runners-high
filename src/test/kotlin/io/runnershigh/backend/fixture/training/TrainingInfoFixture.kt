package io.runnershigh.backend.fixture.training

import io.mockk.every
import io.mockk.mockk
import io.runnershigh.backend.fixture.UserFixture
import io.runnershigh.backend.training.dto.request.SaveTrainingGroup
import io.runnershigh.backend.training.dto.request.SaveTrainingInfo
import io.runnershigh.backend.training.dto.request.SaveTrainingItem
import io.runnershigh.backend.training.dto.response.NextTrainingSchedule
import io.runnershigh.backend.training.dto.response.ReadTrainingSchedule
import io.runnershigh.backend.training.entity.*
import io.runnershigh.backend.user.entity.UserEntity
import net.datafaker.Faker
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

object TrainingInfoFixture {
    private val faker = Faker(Locale.KOREA)

    // ============================================ 엔티티 ============================================
    fun createTrainingSchedule(
        id: Long = 0,
        user: UserEntity = UserFixture.createDefault(),
        title: String = faker.lorem().characters(5, 20),
        location: String = faker.location().publicSpace(),
        description: String = faker.lorem().sentence(30),
        scheduledDateTime: LocalDateTime = LocalDateTime.now(),
        status: TrainingStatus = TrainingStatus.PLANNED,
        color: TrainingColor = TrainingColor.entries.toTypedArray().random(),
    ): TrainingSchedules {
        return TrainingSchedules(
            id = id,
            user = user,
            title = title,
            location = location,
            description = description,
            scheduledDateTime = scheduledDateTime,
            status = status,
            color = color
        )
    }

    fun createEntityMock(
        id: Long,
        user: UserEntity,
        scheduledDateTime: LocalDateTime,
        status: TrainingStatus = TrainingStatus.PLANNED,
    ): TrainingSchedules {
        val dto =
            createReadTrainingSchedule(id, scheduledDateTime = scheduledDateTime, status = status)

        return mockk<TrainingSchedules>().apply {
            every { this@apply.user } returns user
            every { this@apply.id } returns id
            every { this@apply.scheduledDateTime } returns scheduledDateTime
            every { this@apply.status } returns status
            every { this@apply.title } returns dto.title
            every { this@apply.location } returns dto.location
            every { this@apply.description } returns dto.description
            every { this@apply.color } returns dto.color
        }
    }

    private fun createMockTrainingPlanItems(): MutableList<TrainingPlanItems> {
        val item1 = mockk<TrainingPlanItems>().apply {
            every { id } returns 1L
            every { targetDistance } returns 5.0
            every { targetTime } returns Duration.ofMinutes(25)
            every { targetType } returns TargetType.DISTANCE
            every { distanceUnit } returns DistanceUnit.KILOMETER
        }

        val item2 = mockk<TrainingPlanItems>().apply {
            every { id } returns 2L
            every { targetDistance } returns 5.0
            every { targetTime } returns Duration.ofMinutes(25)
            every { targetType } returns TargetType.DISTANCE
            every { distanceUnit } returns DistanceUnit.KILOMETER
        }

        return mutableListOf(item1, item2)
    }

    private fun createMockTrainingPlanGroups(): MutableList<TrainingPlanGroups> {
        val mockGroup = mockk<TrainingPlanGroups>()
        val mockItems = createMockTrainingPlanItems()

        every { mockGroup.id } returns 1L
        every { mockGroup.items } returns mockItems

        return mutableListOf(mockGroup)
    }

    fun createEntityMockWithItems(
        id: Long,
        title: String = faker.lorem().characters(5, 15),
        scheduledDateTime: LocalDateTime,
        user: UserEntity,
        status: TrainingStatus = TrainingStatus.PLANNED,
    ): TrainingSchedules {
        val mockSchedule = mockk<TrainingSchedules>()
        val mockGroups = createMockTrainingPlanGroups()

        every { mockSchedule.id } returns id
        every { mockSchedule.title } returns title
        every { mockSchedule.scheduledDateTime } returns scheduledDateTime
        every { mockSchedule.user } returns user
        every { mockSchedule.status } returns status
        every { mockSchedule.location } returns faker.location().publicSpace()
        every { mockSchedule.description } returns faker.lorem().sentence(20)
        every { mockSchedule.color } returns TrainingColor.entries.toTypedArray().random()
        every { mockSchedule.groups } returns mockGroups

        return mockSchedule
    }

    fun createMultipleEntityMocks(count: Int, user: UserEntity): List<TrainingSchedules> {
        return (1..count).map { index ->
            createEntityMock(
                id = index.toLong(),
                user = user,
                scheduledDateTime = LocalDateTime.now().plusDays(index.toLong() - 1)
            )
        }
    }

    // ============================================ DTO ============================================
    fun createSaveTrainingScheduleList(
        count: Int,
        groupCount: Int = faker.number().numberBetween(1, 3),
        itemsPerGroup: Int = faker.number().numberBetween(1, 3),
    ): List<SaveTrainingInfo> {
        return (1..count).map {
            createSaveTrainingInfo(
                scheduledDateTime = LocalDateTime.now().plusDays(it.toLong()),
                groupCount = groupCount,
                itemsPerGroup = itemsPerGroup
            )
        }
    }

    fun createSaveTrainingInfo(
        title: String = faker.lorem().characters(5, 20),
        location: String = faker.location().publicSpace(),
        description: String = faker.lorem().sentence(30),
        scheduledDateTime: LocalDateTime = LocalDateTime.now()
            .plusDays(faker.number().numberBetween(1, 30).toLong()),
        color: TrainingColor = TrainingColor.entries.toTypedArray().random(),
        groupCount: Int = faker.number().numberBetween(1, 3),
        itemsPerGroup: Int = faker.number().numberBetween(1, 3),
    ): SaveTrainingInfo {
        return SaveTrainingInfo(
            title = title,
            location = location,
            description = description,
            scheduledDateTime = scheduledDateTime,
            color = color,
            groups = createListSaveTrainingGroups(groupCount, itemsPerGroup)
        )
    }

    fun createListSaveTrainingGroups(
        count: Int,
        itemsPerGroup: Int,
    ): List<SaveTrainingGroup> {
        return (1..count).map { groupIndex ->
            createSaveTrainingGroup(groupIndex, itemsPerGroup)
        }
    }

    fun createSaveTrainingGroup(
        groupIndex: Int,
        itemsPerGroup: Int,
    ): SaveTrainingGroup = SaveTrainingGroup(
        groupOrder = groupIndex,
        repeatCount = faker.number().numberBetween(1, 5),
        description = faker.lorem().sentence(10),
        items = createSaveTrainingItems(itemsPerGroup)
    )

    fun createSaveTrainingItems(count: Int): List<SaveTrainingItem> {
        return (1..count).map { itemIndex ->
            val minPace = Duration.ofMinutes(faker.number().numberBetween(7, 15).toLong())
                .plusSeconds(faker.number().numberBetween(0, 59).toLong())
            val maxPace = Duration.ofMinutes(faker.number().numberBetween(3, 6).toLong())
            val avgPace = minPace.plus(maxPace).dividedBy(2)

            SaveTrainingItem(
                itemOrder = itemIndex,
                targetType = TargetType.entries.toTypedArray().random(),
                targetMinPace = minPace,
                targetMaxPace = maxPace,
                targetAvgPace = avgPace,
                runningTypeCode = faker.number().numberBetween(1, 10),
                distanceUnit = DistanceUnit.entries.toTypedArray().random(),
                targetDistance = faker.number().randomDouble(1, 1, 20),
                targetTime = Duration.ofMinutes(faker.number().numberBetween(20, 60).toLong()),
                estimatedDistance = faker.number().randomDouble(1, 1, 20),
                estimatedTime = Duration.ofMinutes(faker.number().numberBetween(20, 60).toLong()),
                note = faker.lorem().sentence(5)
            )
        }
    }

    fun createReadTrainingSchedule(
        id: Long = 0,
        title: String = faker.lorem().characters(5, 15),
        location: String = faker.location().publicSpace(),
        scheduledDateTime: LocalDateTime = LocalDateTime.now(),
        description: String = faker.lorem().sentence(30),
        status: TrainingStatus = TrainingStatus.PLANNED,
        color: TrainingColor = TrainingColor.entries.toTypedArray().random(),
    ): ReadTrainingSchedule {
        return ReadTrainingSchedule(
            id = id,
            title = title,
            location = location,
            scheduledDateTime = scheduledDateTime,
            description = description,
            status = status,
            color = color
        )
    }

    fun createNextTrainingSchedule(
        scheduleId: Long = faker.number().randomNumber(),
        title: String = faker.lorem().characters(5, 15),
        scheduledDateTime: LocalDateTime = LocalDateTime.now()
            .plusDays(faker.number().numberBetween(1, 30).toLong()),
        totalDistance: Double = faker.number().randomDouble(1, 1, 20),
        totalTime: Duration = Duration.ofMinutes(faker.number().numberBetween(1, 10).toLong()),
        trainingColor: TrainingColor = TrainingColor.entries.toTypedArray().random(),
    ) = NextTrainingSchedule(
        scheduleId = scheduleId,
        title = title,
        scheduledDateTime = scheduledDateTime,
        totalDistance = totalDistance,
        totalTime = totalTime,
        trainingColor = trainingColor
    )

    fun createSaveTrainingPlanItem(
        itemOrder: Int = faker.number().numberBetween(1, 9),
        targetType: TargetType = TargetType.entries.toTypedArray().random(),
        targetMinPace: Duration = Duration.ofMinutes(
            faker.random().nextLong(8, 15)
        ),
        targetMaxPace: Duration = Duration.ofMinutes(
            faker.random().nextLong(3, 5)
        ),
        targetAvgPace: Duration = targetMinPace.plus(targetMaxPace).dividedBy(2),
        runningTypeCode: Int = 101,
        distanceUnit: DistanceUnit = DistanceUnit.KILOMETER,
        targetDistance: Double = faker.number().randomDouble(1, 5, 30),
        targetTime: Duration = Duration.ofMinutes(
            faker.random().nextLong(25, 59)
        ),
        estimatedDistance: Double = faker.number().randomDouble(1, 5, 30),
        estimatedTime: Duration = Duration.ofMinutes(
            faker.random().nextLong(25, 59)
        ),
        note: String? = faker.lorem().word(),
    ) = SaveTrainingItem(
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
        note = note,
    )

}