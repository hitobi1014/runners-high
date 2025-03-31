package io.runnershigh.backend.training.infrastructure.repository.command

import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import org.springframework.data.jpa.repository.JpaRepository

interface TrainingSchedulesCommandRepository : JpaRepository<TrainingSchedules, Long>