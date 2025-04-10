package io.runnershigh.backend.training.infrastructure.repository

import io.runnershigh.backend.training.infrastructure.entity.TrainingPlanItems
import org.springframework.data.jpa.repository.JpaRepository

interface TrainingPlanItemsRepository : JpaRepository<TrainingPlanItems, Long>