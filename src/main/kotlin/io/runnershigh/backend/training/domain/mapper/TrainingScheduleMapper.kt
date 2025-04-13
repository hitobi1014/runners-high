package io.runnershigh.backend.training.domain.mapper

import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.training.ui.dto.request.SaveTrainingSchedule
import io.runnershigh.backend.training.ui.dto.response.ReadTrainingSchedule
import io.runnershigh.backend.user.infrastructure.entity.UserEntity

// request
/**
 * Converts a SaveTrainingSchedule DTO into a TrainingSchedules entity.
 *
 * Maps the DTO's fields—title, location, scheduled date, description, and status—to a new TrainingSchedules
 * instance and associates it with the provided user.
 *
 * @param user The user associated with the training schedule.
 * @return A TrainingSchedules entity containing the training schedule details.
 */
fun SaveTrainingSchedule.toEntity(user: UserEntity) = TrainingSchedules(
    user = user,
    title = this.title,
    location = this.location,
    scheduledDate = this.scheduledDate,
    description = this.description,
    status = this.status
)

/**
 * Converts a TrainingSchedules entity to a ReadTrainingSchedule DTO.
 *
 * Maps the entity's id, title, location, scheduledDate, description, and status into a new ReadTrainingSchedule.
 *
 * @return a ReadTrainingSchedule instance representing the entity.
 */
fun TrainingSchedules.toDto() = ReadTrainingSchedule(
    id = this.id,
    title = this.title,
    location = this.location,
    scheduledDate = this.scheduledDate,
    description = this.description,
    status = this.status
)