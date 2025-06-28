package io.runnershigh.backend.training.service

import io.runnershigh.backend.training.dto.request.SaveTrainingSchedule
import io.runnershigh.backend.training.dto.response.NextTrainingSchedule
import io.runnershigh.backend.training.dto.response.ReadTrainingSchedule
import io.runnershigh.backend.training.entity.TrainingSchedules

interface TrainingSchedulesService {
    fun createTrainingSchedule(dto: SaveTrainingSchedule): TrainingSchedules
    fun getTrainingSchedules(): List<ReadTrainingSchedule>
    fun getCurrentWeekTrainingSchedules(): List<ReadTrainingSchedule>
    fun getNextUpcomingTrainingSchedule(): NextTrainingSchedule?
}