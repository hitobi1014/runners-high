package io.runnershigh.backend.training.repository

import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.training.repository.querydsl.TrainingScheduleRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository

interface TrainingSchedulesRepository : JpaRepository<TrainingSchedules, Long>,
    TrainingScheduleRepositoryCustom