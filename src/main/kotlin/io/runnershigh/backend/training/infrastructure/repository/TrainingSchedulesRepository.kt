package io.runnershigh.backend.training.infrastructure.repository

import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import org.springframework.data.jpa.repository.JpaRepository

interface TrainingSchedulesRepository : JpaRepository<TrainingSchedules, Long>