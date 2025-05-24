package io.runnershigh.backend.training.service

import io.runnershigh.backend.training.dto.request.SaveTrainingPlanItem
import io.runnershigh.backend.training.entity.TrainingPlanItems

interface TrainingPlanItemsService {
    fun createTrainingItems(scheduleId: Long, dto: SaveTrainingPlanItem): TrainingPlanItems
}