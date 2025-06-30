package io.runnershigh.backend.fixture.training

import io.mockk.every
import io.mockk.mockk
import io.runnershigh.backend.fixture.UserFixture
import io.runnershigh.backend.training.entity.enum.TrainingStatus
import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.training.entity.TrainingPlanItems
import io.runnershigh.backend.training.entity.enum.DistanceUnit
import io.runnershigh.backend.training.entity.enum.TargetType
import io.runnershigh.backend.training.dto.response.ReadTrainingSchedule
import io.runnershigh.backend.user.entity.UserEntity
import net.datafaker.Faker
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

object TrainingScheduleFixture {
    private val faker = Faker(Locale.KOREA)

    fun createDefault(
        id: Long = 0,
        user: UserEntity = UserFixture.createDefault(),
        title: String = faker.lorem().characters(5, 20),
        location: String = faker.location().publicSpace(),
        description: String = faker.lorem().sentence(30),
        scheduledDate: LocalDate = LocalDate.now(),
        status: TrainingStatus = TrainingStatus.PLANNED,
        color: String = "#B5EAD7",
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

    fun createTrainingScheduleList(count: Int, user: UserEntity): List<TrainingSchedules> {
        return (1..count).map {
            createDefault(
                user = user,
                scheduledDate = LocalDate.now().plusDays(it.toLong() - 1),
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
        color: String = "#B5EAD7",
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
        val mockItems = createMockTrainingItems(mockSchedule)

        every { mockSchedule.id } returns id
        every { mockSchedule.title } returns title
        every { mockSchedule.scheduledDate } returns scheduledDate
        every { mockSchedule.user } returns user
        every { mockSchedule.status } returns status
        every { mockSchedule.location } returns faker.location().publicSpace()
        every { mockSchedule.description } returns faker.lorem().sentence(20)
        every { mockSchedule.color } returns "#B5EAD7"
        every { mockSchedule.items } returns mockItems

        return mockSchedule
    }

    private fun createMockTrainingItems(schedule: TrainingSchedules): MutableList<TrainingPlanItems> {
        val item1 = mockk<TrainingPlanItems>().apply {
            every { id } returns 1L
            every { this@apply.schedule } returns schedule
            every { targetDistance } returns 5.0
            every { targetTime } returns LocalTime.of(0, 25)
            every { targetType } returns TargetType.DISTANCE
            every { distanceUnit } returns DistanceUnit.KILOMETER
        }

        val item2 = mockk<TrainingPlanItems>().apply {
            every { id } returns 2L
            every { this@apply.schedule } returns schedule
            every { targetDistance } returns 5.0
            every { targetTime } returns LocalTime.of(0, 25)
            every { targetType } returns TargetType.DISTANCE
            every { distanceUnit } returns DistanceUnit.KILOMETER
        }

        return mutableListOf(item1, item2)
    }
}