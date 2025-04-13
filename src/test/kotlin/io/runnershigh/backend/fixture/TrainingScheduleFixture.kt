package io.runnershigh.backend.fixture

import io.mockk.every
import io.mockk.mockk
import io.runnershigh.backend.training.domain.enum.TrainingStatus
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.training.ui.dto.response.ReadTrainingSchedule
import io.runnershigh.backend.user.infrastructure.entity.UserEntity
import java.time.LocalDate

object TrainingScheduleFixture {
    /**
     * Creates a default training schedule instance for testing.
     *
     * This function constructs a new [TrainingSchedules] object using default values that can be
     * overridden via its parameters. It leverages [UserFixture.createDefault] to assign a default
     * user entity.
     *
     * @param id the training schedule identifier (default is 0).
     * @param user the user entity associated with the training schedule.
     * @param title the title of the training session (default is "하프마라톤 대비").
     * @param location the location of the training session (default is "보라매 공원").
     * @param description a brief description of the training session (default is "보라매 공원 가볍게 러닝").
     * @param scheduledDate the date on which the training session is scheduled (default is the current date).
     * @param status the status of the training session (default is [TrainingStatus.PLANNED]).
     * @return a new instance of [TrainingSchedules] initialized with the provided or default values.
     */
    fun createDefault(
        id: Long = 0,
        user: UserEntity = UserFixture.createDefault(),
        title: String = "하프마라톤 대비",
        location: String = "보라매 공원",
        description: String = "보라매 공원 가볍게 러닝",
        scheduledDate: LocalDate = LocalDate.now(),
        status: TrainingStatus = TrainingStatus.PLANNED,
    ): TrainingSchedules {
        return TrainingSchedules(
            id = id,
            user = user,
            title = title,
            location = location,
            description = description,
            scheduledDate = scheduledDate,
            status = status,
        )
    }

    /**
     * Creates a [ReadTrainingSchedule] instance with the given or default values.
     *
     * This function returns a data transfer object (DTO) representing a training schedule
     * populated with the specified parameters or their defaults.
     *
     * @param id the unique identifier for the training schedule (default is 1L).
     * @param title the title of the training schedule (default is "훈련1").
     * @param location the location where the training is held (default is "여의도 공원").
     * @param scheduledDate the date on which the training is scheduled (default is the current date).
     * @param description a brief description of the training (default is "고구마 캐기").
     * @param status the current status of the training (default is [TrainingStatus.PLANNED]).
     * @return a new [ReadTrainingSchedule] instance populated with the provided values.
     */
    fun createReadTrainingSchedule(
        id: Long = 1L,
        title: String = "훈련1",
        location: String = "여의도 공원",
        scheduledDate: LocalDate = LocalDate.now(),
        description: String = "고구마 캐기",
        status: TrainingStatus = TrainingStatus.PLANNED,
    ): ReadTrainingSchedule {
        return ReadTrainingSchedule(
            id = id,
            title = title,
            location = location,
            scheduledDate = scheduledDate,
            description = description,
            status = status,
        )
    }

    /**
     * Creates a mock TrainingSchedules entity with preset property values.
     *
     * This function initializes a DTO using the provided [id], [scheduledDate], and [status] parameters.
     * It then configures a mock TrainingSchedules instance to return these values along with the 
     * DTO-derived [title], [location], and [description] for testing purposes.
     *
     * @param id Unique identifier for the training schedule.
     * @param scheduledDate The training's scheduled date.
     * @param status The training status (default is PLANNED).
     * @return A mock TrainingSchedules entity with configured properties.
     */
    fun createEntityMock(
        id: Long,
        scheduledDate: LocalDate,
        status: TrainingStatus = TrainingStatus.PLANNED,
    ): TrainingSchedules {
        val dto = createReadTrainingSchedule(id, scheduledDate = scheduledDate, status = status)

        return mockk<TrainingSchedules>().apply {
            every { this@apply.id } returns id
            every { this@apply.scheduledDate } returns scheduledDate
            every { this@apply.status } returns status
            every { this@apply.title } returns dto.title
            every { this@apply.location } returns dto.location
            every { this@apply.description } returns dto.description
        }
    }

    /**
     * Generates a list of mock TrainingSchedules entities.
     *
     * Each mock is created with a sequential ID (starting at 1) and a scheduled date
     * incremented by one day for every subsequent mock, beginning from the current date.
     *
     * @param count the number of mocks to generate.
     * @return a list of mock TrainingSchedules objects.
     */
    fun createMultipleEntityMocks(count: Int): List<TrainingSchedules> {
        return (1..count).map { index ->
            createEntityMock(
                index.toLong(),
                scheduledDate = LocalDate.now().plusDays(index.toLong() - 1)
            )
        }
    }
}