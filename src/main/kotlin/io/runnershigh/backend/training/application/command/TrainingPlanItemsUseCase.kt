package io.runnershigh.backend.training.application.command

import io.runnershigh.backend.training.domain.mapper.toEntity
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.training.infrastructure.repository.command.TrainingPlanItemsRepository
import io.runnershigh.backend.training.infrastructure.repository.command.TrainingSchedulesCommandRepository
import io.runnershigh.backend.training.ui.dto.SaveTrainingPlanItem
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TrainingPlanItemsUseCase(
    private val trainingPlanItemsRepository: TrainingPlanItemsRepository,
    private val trainingSchedulesRepository: TrainingSchedulesCommandRepository,
) {

    fun createTrainingItems(scheduleId: Long, dto: SaveTrainingPlanItem) {
        val entity = dto.toEntity(getSchedule(scheduleId))
        trainingPlanItemsRepository.save(entity)
    }

    private fun getSchedule(scheduleId: Long): TrainingSchedules {
        // TODO 에러 메세지 변경
        return trainingSchedulesRepository.findById(scheduleId)
            .orElseThrow { IllegalArgumentException("Schedule with id $scheduleId not found") }
    }
}