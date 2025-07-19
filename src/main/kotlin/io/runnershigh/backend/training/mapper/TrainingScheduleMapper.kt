package io.runnershigh.backend.training.mapper

import io.runnershigh.backend.training.dto.request.SaveTrainingInfo
import io.runnershigh.backend.training.dto.response.ReadTrainingSchedule
import io.runnershigh.backend.training.dto.response.WeeklyTrainingSchedule
import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.user.entity.UserEntity
import java.time.Duration
import java.time.format.TextStyle
import java.util.*

// request => toEntity
fun SaveTrainingInfo.toEntity(user: UserEntity) = TrainingSchedules(
    user = user,
    title = this.title,
    location = this.location,
    scheduledDateTime = this.scheduledDateTime,
    description = this.description,
    status = this.status,
    color = this.color
)

// response => toDTO
fun TrainingSchedules.toReadTrainingSchedule() = ReadTrainingSchedule(
    id = this.id,
    title = this.title,
    location = this.location,
    scheduledDateTime = this.scheduledDateTime,
    description = this.description,
    status = this.status,
    color = this.color
)

fun TrainingSchedules.toWeeklyTrainingSchedule() = WeeklyTrainingSchedule(
    scheduleId = this.id,
    title = this.title,
    scheduledDateTime = this.scheduledDateTime,
    dayOfWeek = this.scheduledDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN),
    estimatedDistance = this.groups.flatMap { it.items }.sumOf { it.estimatedDistance },
    estimatedTime = this.groups.flatMap { it.items }
        .map { it.estimatedTime }
        .fold(Duration.ZERO) { acc, dur -> acc.plus(dur) },
    status = this.status,
    trainingColor = this.color,

    )
