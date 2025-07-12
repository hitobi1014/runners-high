package io.runnershigh.backend.training.service

import io.runnershigh.backend.training.dto.request.SaveTrainingInfo
import io.runnershigh.backend.training.dto.response.NextTrainingSchedule
import io.runnershigh.backend.training.dto.response.ReadTrainingSchedule
import io.runnershigh.backend.training.dto.response.SummaryThisWeekSchedule
import io.runnershigh.backend.training.entity.TrainingSchedules

interface TrainingSchedulesService {
    fun createTrainingSchedule(dto: SaveTrainingInfo): TrainingSchedules
    fun getTrainingSchedules(): List<ReadTrainingSchedule>
    fun getCurrentWeekTrainingSchedules(): List<ReadTrainingSchedule>
    fun getNextUpcomingTrainingSchedule(): NextTrainingSchedule?
    fun getSummaryThisWeekForSchedule(): SummaryThisWeekSchedule
}