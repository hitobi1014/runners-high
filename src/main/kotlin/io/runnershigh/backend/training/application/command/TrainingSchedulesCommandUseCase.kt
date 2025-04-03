package io.runnershigh.backend.training.application.command

import io.runnershigh.backend.training.domain.mapper.toEntity
import io.runnershigh.backend.training.exception.TrainingException
import io.runnershigh.backend.training.exception.TrainingExceptionType
import io.runnershigh.backend.training.infrastructure.repository.command.TrainingSchedulesCommandRepository
import io.runnershigh.backend.training.ui.dto.SaveTrainingSchedule
import io.runnershigh.backend.user.util.LoginUserContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class TrainingSchedulesCommandUseCase(
    private val repository: TrainingSchedulesCommandRepository,
    private val loginUserContext: LoginUserContext,
) {

    fun createTrainingSchedule(dto: SaveTrainingSchedule) {
        // step01. 훈련일자 값 검증 -> 훈련일자가 현재보다 이전일 수 없음
        validateTrainingTime(dto.scheduledDate)

        repository.save(dto.toEntity(loginUserContext.getCurrentUser()))
    }

    private fun validateTrainingTime(schedule: LocalDate) {
        val now = LocalDate.now()

        if (schedule.isBefore(now)) {
            throw TrainingException(TrainingExceptionType.CANNOT_REGISTER_PAST_TRAINING)
        }
    }
}