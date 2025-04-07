package io.runnershigh.backend.training.application.command

import io.runnershigh.backend.training.domain.mapper.toEntity
import io.runnershigh.backend.training.exception.TrainingException
import io.runnershigh.backend.training.exception.TrainingExceptionType
import io.runnershigh.backend.training.infrastructure.entity.TrainingPlanItems
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.training.infrastructure.repository.command.TrainingPlanItemsRepository
import io.runnershigh.backend.training.infrastructure.repository.command.TrainingSchedulesCommandRepository
import io.runnershigh.backend.training.ui.dto.SaveTrainingPlanItem
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TrainingPlanItemsUseCase(
    private val trainingPlanItemsRepository: TrainingPlanItemsRepository,
    private val trainingSchedulesRepository: TrainingSchedulesCommandRepository,
) {

    fun createTrainingItems(scheduleId: Long, dto: SaveTrainingPlanItem): TrainingPlanItems {
        val entity = dto.toEntity(getSchedule(scheduleId))
        return trainingPlanItemsRepository.save(entity)
    }

    private fun getSchedule(scheduleId: Long): TrainingSchedules {
        return trainingSchedulesRepository.findByIdOrNull(scheduleId)
            ?: throw TrainingException(TrainingExceptionType.CANNOT_FOUND_TRAINING_SCHEDULE)
    }
}