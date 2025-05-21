package io.runnershigh.backend.training.repository

import io.runnershigh.backend.training.entity.TrainingPlanGroups
import org.springframework.data.jpa.repository.JpaRepository

interface TrainingPlanGroupRepository : JpaRepository<TrainingPlanGroups, Long>