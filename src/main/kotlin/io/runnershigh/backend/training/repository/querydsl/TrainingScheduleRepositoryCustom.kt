package io.runnershigh.backend.training.repository.querydsl

import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.user.entity.UserEntity
import java.time.LocalDateTime

interface TrainingScheduleRepositoryCustom {
    fun retrieveTrainingSchedules(user: UserEntity): List<TrainingSchedules>
    fun retrieveNextUpcomingSchedule(user: UserEntity): TrainingSchedules?
    fun findThisWeekTrainingSchedules(
        user: UserEntity,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        plannedOnly: Boolean = false,
    ): List<TrainingSchedules>
}