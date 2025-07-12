package io.runnershigh.backend.fixture.training

import io.mockk.every
import io.mockk.mockk
import io.runnershigh.backend.training.dto.response.SummaryThisWeekSchedule
import io.runnershigh.backend.training.entity.TrainingPlanGroups
import io.runnershigh.backend.training.entity.TrainingPlanItems
import io.runnershigh.backend.training.entity.TrainingSchedules
import net.datafaker.Faker
import java.time.Duration
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


}