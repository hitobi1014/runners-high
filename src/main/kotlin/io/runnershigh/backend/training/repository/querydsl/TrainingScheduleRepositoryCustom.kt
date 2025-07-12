package io.runnershigh.backend.training.repository.querydsl

import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.user.entity.UserEntity
import java.time.LocalDate

interface TrainingScheduleRepositoryCustom {
    fun retrieveTrainingSchedules(user: UserEntity): List<TrainingSchedules>
    fun retrieveCurrentWeekSchedules(
        user: UserEntity,
        previousSunday: LocalDate,
        nextSaturday: LocalDate,
    ): List<TrainingSchedules>

    fun retrieveNextUpcomingSchedule(user: UserEntity): TrainingSchedules?

    fun findThisWeekTrainingSchedules(
        user: UserEntity,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<TrainingSchedules>
}