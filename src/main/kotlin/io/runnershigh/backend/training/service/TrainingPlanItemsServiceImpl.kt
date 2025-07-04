package io.runnershigh.backend.training.service

import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.training.exception.TrainingException
import io.runnershigh.backend.training.exception.TrainingExceptionType
import io.runnershigh.backend.training.repository.TrainingPlanItemsRepository
import io.runnershigh.backend.training.repository.TrainingSchedulesRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class TrainingPlanItemsServiceImpl(
    private val trainingPlanItemsRepository: TrainingPlanItemsRepository,
    private val trainingSchedulesRepository: TrainingSchedulesRepository,
) : TrainingPlanItemsService {

//    override fun createTrainingItems(
//        scheduleId: Long,
//        dto: SaveTrainingPlanItem,
//    ): TrainingPlanItems {
//        val entity = dto.toEntity(getSchedule(scheduleId))
//        return trainingPlanItemsRepository.save(entity)
//    }

    private fun getSchedule(scheduleId: Long): TrainingSchedules {
        return trainingSchedulesRepository.findByIdOrNull(scheduleId)
            ?: throw TrainingException(TrainingExceptionType.CANNOT_FOUND_TRAINING_SCHEDULE)
    }
}