package io.runnershigh.backend.training.application

import io.runnershigh.backend.training.domain.mapper.toEntity
import io.runnershigh.backend.training.exception.TrainingException
import io.runnershigh.backend.training.exception.TrainingExceptionType
import io.runnershigh.backend.training.infrastructure.entity.TrainingPlanGroups
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.training.infrastructure.repository.TrainingPlanGroupRepository
import io.runnershigh.backend.training.infrastructure.repository.TrainingSchedulesRepository
import io.runnershigh.backend.training.ui.dto.SaveTrainingPlanGroup
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TrainingPlanGroupUseCase(
    private val trainingPlanGroupRepository: TrainingPlanGroupRepository,
    private val trainingSchedulesRepository: TrainingSchedulesRepository,
) {

    fun createTrainingPlanGroup(dto: SaveTrainingPlanGroup, scheduleId: Long): TrainingPlanGroups {
        val scheduleEntity = getTrainingSchedule(scheduleId)
        val trainingPlanGroupEntity = dto.toEntity(scheduleEntity)

        return trainingPlanGroupRepository.save(trainingPlanGroupEntity)
    }

    private fun getTrainingSchedule(id: Long): TrainingSchedules {
        return trainingSchedulesRepository.findByIdOrNull(id) ?: throw TrainingException(
            TrainingExceptionType.CANNOT_FOUND_TRAINING_SCHEDULE
        )
    }

}