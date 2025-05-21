package io.runnershigh.backend.training.repository

import io.runnershigh.backend.training.entity.TrainingPlanItems
import org.springframework.data.jpa.repository.JpaRepository

interface TrainingPlanItemsRepository : JpaRepository<TrainingPlanItems, Long>