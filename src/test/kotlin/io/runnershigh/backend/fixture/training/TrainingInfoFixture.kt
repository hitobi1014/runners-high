package io.runnershigh.backend.fixture.training

import io.mockk.every
import io.mockk.mockk
import io.runnershigh.backend.fixture.UserFixture
import io.runnershigh.backend.training.dto.request.SaveTrainingGroup
import io.runnershigh.backend.training.dto.request.SaveTrainingInfo
import io.runnershigh.backend.training.dto.request.SaveTrainingItem
import io.runnershigh.backend.training.dto.response.NextTrainingSchedule
import io.runnershigh.backend.training.dto.response.ReadTrainingSchedule
import io.runnershigh.backend.training.entity.TrainingPlanGroups
import io.runnershigh.backend.training.entity.TrainingPlanItems
import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.training.entity.enum.DistanceUnit
import io.runnershigh.backend.training.entity.enum.TargetType
import io.runnershigh.backend.training.entity.enum.TrainingColor
import io.runnershigh.backend.training.entity.enum.TrainingStatus
import io.runnershigh.backend.user.entity.UserEntity
import net.datafaker.Faker
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

object TrainingInfoFixture {
    private val faker = Faker(Locale.KOREA)

    fun createDefault(
        id: Long = 0,
        user: UserEntity = UserFixture.createDefault(),
        title: String = faker.lorem().characters(5, 20),
        location: String = faker.location().publicSpace(),
        description: String = faker.lorem().sentence(30),
        scheduledDate: LocalDate = LocalDate.now(),
        status: TrainingStatus = TrainingStatus.PLANNED,
        color: TrainingColor = TrainingColor.entries.toTypedArray().random(),
    ): TrainingSchedules {
        return TrainingSchedules(
            id = id,
            user = user,
            title = title,
            location = location,
            description = description,
            scheduledDate = scheduledDate,
            status = status,
            color = color
        )
    }

    fun createSaveTrainingScheduleList(
        count: Int,
        groupCount: Int = faker.number().numberBetween(1, 3),
        itemsPerGroup: Int = faker.number().numberBetween(1, 3),
    ): List<SaveTrainingInfo> {
        return (1..count).map {
            createSaveTrainingInfo(
                scheduledDate = LocalDate.now().plusDays(it.toLong()),
                groupCount = groupCount,
                itemsPerGroup = itemsPerGroup
            )
        }
    }

    fun createSaveTrainingInfo(
        title: String = faker.lorem().characters(5, 20),
        location: String = faker.location().publicSpace(),
        description: String = faker.lorem().sentence(30),
        scheduledDate: LocalDate = LocalDate.now()
            .plusDays(faker.number().numberBetween(1, 30).toLong()),
        color: TrainingColor = TrainingColor.entries.toTypedArray().random(),
        groupCount: Int = faker.number().numberBetween(1, 3),
        itemsPerGroup: Int = faker.number().numberBetween(1, 3),
    ): SaveTrainingInfo {
        return SaveTrainingInfo(
            title = title,
            location = location,
            description = description,
            scheduledDate = scheduledDate,
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
            val minPace = LocalTime.of(
                0,
                faker.number().numberBetween(4, 6),
                faker.number().numberBetween(0, 59)
            )
            val maxPace = minPace.plusMinutes(faker.number().numberBetween(10, 30).toLong())
            val avgPace = LocalTime.of(
                0,
                (minPace.minute + maxPace.minute) / 2,
                (minPace.second + maxPace.second) / 2
            )

            SaveTrainingItem(
                itemOrder = itemIndex,
                targetType = TargetType.entries.toTypedArray().random(),
                targetMinPace = minPace,
                targetMaxPace = maxPace,
                targetAvgPace = avgPace,
                runningTypeCode = faker.number().numberBetween(1, 10),
                distanceUnit = DistanceUnit.entries.toTypedArray().random(),
                targetDistance = faker.number().randomDouble(1, 1, 20),
                targetTime = LocalTime.of(0, faker.number().numberBetween(20, 60)),
                estimatedDistance = faker.number().randomDouble(1, 1, 20),
                estimatedTime = LocalTime.of(0, faker.number().numberBetween(20, 60)),
                note = faker.lorem().sentence(5)
            )
        }
    }

    fun createReadTrainingSchedule(
        id: Long = 0,
        title: String = faker.lorem().characters(5, 15),
        location: String = faker.location().publicSpace(),
        scheduledDate: LocalDate = LocalDate.now(),
        description: String = faker.lorem().sentence(30),
        status: TrainingStatus = TrainingStatus.PLANNED,
        color: TrainingColor = TrainingColor.entries.toTypedArray().random(),
    ): ReadTrainingSchedule {
        return ReadTrainingSchedule(
            id = id,
            title = title,
            location = location,
            scheduledDate = scheduledDate,
            description = description,
            status = status,
            color = color
        )
    }

    fun createNextTrainingSchedule(
        scheduleId: Long = faker.number().randomNumber(),
        title: String = faker.lorem().characters(5, 15),
        scheduledDate: LocalDate = LocalDate.now()
            .plusDays(faker.number().numberBetween(1, 30).toLong()),
        totalDistance: Double = faker.number().randomDouble(1, 1, 20),
        totalTime: LocalTime = LocalTime.of(0, faker.number().numberBetween(20, 60)),
    ) = NextTrainingSchedule(
        scheduleId = scheduleId,
        title = title,
        scheduledDate = scheduledDate,
        totalDistance = totalDistance,
        totalTime = totalTime
    )


    fun createEntityMock(
        id: Long,
        user: UserEntity,
        scheduledDate: LocalDate,
        status: TrainingStatus = TrainingStatus.PLANNED,
    ): TrainingSchedules {
        val dto = createReadTrainingSchedule(id, scheduledDate = scheduledDate, status = status)

        return mockk<TrainingSchedules>().apply {
            every { this@apply.user } returns user
            every { this@apply.id } returns id
            every { this@apply.scheduledDate } returns scheduledDate
            every { this@apply.status } returns status
            every { this@apply.title } returns dto.title
            every { this@apply.location } returns dto.location
            every { this@apply.description } returns dto.description
            every { this@apply.color } returns dto.color
        }
    }

    fun createMultipleEntityMocks(count: Int, user: UserEntity): List<TrainingSchedules> {
        return (1..count).map { index ->
            createEntityMock(
                id = index.toLong(),
                user = user,
                scheduledDate = LocalDate.now().plusDays(index.toLong() - 1)
            )
        }
    }

    fun createEntityMockWithItems(
        id: Long,
        title: String = faker.lorem().characters(5, 15),
        scheduledDate: LocalDate,
        user: UserEntity,
        status: TrainingStatus = TrainingStatus.PLANNED,
    ): TrainingSchedules {
        val mockSchedule = mockk<TrainingSchedules>()
        val mockGroups = createMockTrainingPlanGroups()

        every { mockSchedule.id } returns id
        every { mockSchedule.title } returns title
        every { mockSchedule.scheduledDate } returns scheduledDate
        every { mockSchedule.user } returns user
        every { mockSchedule.status } returns status
        every { mockSchedule.location } returns faker.location().publicSpace()
        every { mockSchedule.description } returns faker.lorem().sentence(20)
        every { mockSchedule.color } returns TrainingColor.entries.toTypedArray().random()
        every { mockSchedule.groups } returns mockGroups

        return mockSchedule
    }

    private fun createMockTrainingPlanGroups(): MutableList<TrainingPlanGroups> {
        val mockGroup = mockk<TrainingPlanGroups>()
        val mockItems = createMockTrainingPlanItems()

        every { mockGroup.id } returns 1L
        every { mockGroup.items } returns mockItems

        return mutableListOf(mockGroup)
    }

    private fun createMockTrainingPlanItems(): MutableList<TrainingPlanItems> {
        val item1 = mockk<TrainingPlanItems>().apply {
            every { id } returns 1L
            every { targetDistance } returns 5.0
            every { targetTime } returns LocalTime.of(0, 25)
            every { targetType } returns TargetType.DISTANCE
            every { distanceUnit } returns DistanceUnit.KILOMETER
        }

        val item2 = mockk<TrainingPlanItems>().apply {
            every { id } returns 2L
            every { targetDistance } returns 5.0
            every { targetTime } returns LocalTime.of(0, 25)
            every { targetType } returns TargetType.DISTANCE
            every { distanceUnit } returns DistanceUnit.KILOMETER
        }

        return mutableListOf(item1, item2)
    }

//    private fun createMockTrainingPlanItems(schedule: TrainingSchedules): MutableList<TrainingPlanItems> {
//        val mockGroup = mockk<TrainingPlanGroups>()
//
//        val item1 = mockk<TrainingPlanItems>().apply {
//            every { id } returns 1L
//            every { group } returns mockGroup
//            every { targetDistance } returns 5.0
//            every { targetTime } returns LocalTime.of(0, 25)
//            every { targetType } returns TargetType.DISTANCE
//            every { distanceUnit } returns DistanceUnit.KILOMETER
//        }
//
//        val item2 = mockk<TrainingPlanItems>().apply {
//            every { id } returns 2L
//            every { group } returns mockGroup
//            every { targetDistance } returns 5.0
//            every { targetTime } returns LocalTime.of(0, 25)
//            every { targetType } returns TargetType.DISTANCE
//            every { distanceUnit } returns DistanceUnit.KILOMETER
//        }
//
//        return mutableListOf(item1, item2)
//    }
}