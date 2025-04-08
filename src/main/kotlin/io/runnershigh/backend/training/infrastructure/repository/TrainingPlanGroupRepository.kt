package io.runnershigh.backend.training.infrastructure.repository

import io.runnershigh.backend.training.infrastructure.entity.TrainingPlanGroups
import org.springframework.data.jpa.repository.JpaRepository

interface TrainingPlanGroupRepository : JpaRepository<TrainingPlanGroups, Long>