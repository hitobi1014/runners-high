package io.runnershigh.backend.training.service

import io.runnershigh.backend.training.dto.request.SaveTrainingPlanGroup
import io.runnershigh.backend.training.entity.TrainingPlanGroups

interface TrainingPlanGroupService {
    fun createTrainingPlanGroup(dto: SaveTrainingPlanGroup, scheduleId: Long): TrainingPlanGroups
}