package io.runnershigh.backend.fixture.training

import io.mockk.every
import io.mockk.mockk
import io.runnershigh.backend.training.dto.response.SummaryThisWeekSchedule
import io.runnershigh.backend.training.dto.response.WeeklyTrainingSchedule
import io.runnershigh.backend.training.entity.TrainingColor
import io.runnershigh.backend.training.entity.TrainingPlanGroups
import io.runnershigh.backend.training.entity.TrainingPlanItems
import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.training.entity.TrainingStatus
import net.datafaker.Faker
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*

object TrainingScheduleFixture {
    private val faker = Faker(Locale.KOREA)

    // ============================================= DTO =============================================
    fun createMockSummaryThisWeekSchedule(
        scheduleCount: Int = faker.random().nextInt(1, 10),
        totalDistance: Double = faker.random().nextDouble(1.0, 100.0),
        totalTime: Duration = Duration.ofHours(faker.random().nextLong(1, 10))
            .plusMinutes(faker.random().nextLong(1, 60)),
    ) = SummaryThisWeekSchedule(
        scheduleCount = scheduleCount,
        totalDistance = totalDistance,
        totalTime = totalTime
    )

    fun createMockTrainingSchedule(
        estimatedDistance: Double = faker.random().nextDouble(1.0, 100.0),
        estimatedTime: Duration = Duration.ofHours(faker.random().nextLong(1, 10))
            .plusMinutes(faker.random().nextLong(1, 60)),
    ): TrainingSchedules {
        val mockItem = mockk<TrainingPlanItems>()
        every { mockItem.estimatedDistance } returns estimatedDistance
        every { mockItem.estimatedTime } returns estimatedTime

        val mockGroup = mockk<TrainingPlanGroups>()
        every { mockGroup.items } returns mutableListOf(mockItem)

        val mockSchedule = mockk<TrainingSchedules>()
        every { mockSchedule.groups } returns mutableListOf(mockGroup)

        return mockSchedule
    }

    fun createMockWeeklyTrainingSchedule(
        scheduleId: Long = faker.random().nextLong(1000),
        title: String = faker.lorem().characters(5, 15),
        scheduledDateTime: LocalDateTime = LocalDateTime.now()
            .plusDays(faker.random().nextLong(0, 7)),
        dayOfWeek: String = scheduledDateTime.dayOfWeek.getDisplayName(
            TextStyle.FULL,
            Locale.KOREAN
        ),
        distance: Double = faker.random().nextDouble(1.0, 50.0),
        status: TrainingStatus = TrainingStatus.entries.toTypedArray().random(),
        trainingColor: TrainingColor = TrainingColor.entries.toTypedArray().random(),
    ) = WeeklyTrainingSchedule(
        scheduleId = scheduleId,
        title = title,
        dayOfWeek = dayOfWeek,
        distance = distance,
        status = status,
        scheduledDateTime = scheduledDateTime,
        trainingColor = trainingColor
    )

    fun createMockTrainingScheduleForWeekly(
        id: Long = faker.random().nextLong(1000),
        title: String = faker.lorem().characters(5, 15),
        scheduledDateTime: LocalDateTime = LocalDateTime.now(),
        status: TrainingStatus = TrainingStatus.PLANNED,
        trainingColor: TrainingColor = TrainingColor.entries.toTypedArray().random(),
    ): TrainingSchedules {
        val mockSchedule = mockk<TrainingSchedules>()
        val mockGroup = mockk<TrainingPlanGroups>()
        val mockItem = mockk<TrainingPlanItems>()

        every { mockItem.estimatedDistance } returns faker.random().nextDouble(1.0, 50.0)
        every { mockGroup.items } returns mutableListOf(mockItem)

        every { mockSchedule.id } returns id
        every { mockSchedule.title } returns title
        every { mockSchedule.scheduledDateTime } returns scheduledDateTime
        every { mockSchedule.status } returns status
        every { mockSchedule.groups } returns mutableListOf(mockGroup)
        every { mockSchedule.color } returns trainingColor

        return mockSchedule
    }
}