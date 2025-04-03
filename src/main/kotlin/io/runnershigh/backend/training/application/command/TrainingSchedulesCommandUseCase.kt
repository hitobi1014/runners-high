package io.runnershigh.backend.training.application.command

import io.runnershigh.backend.training.domain.mapper.toEntity
import io.runnershigh.backend.training.exception.TrainingException
import io.runnershigh.backend.training.exception.TrainingExceptionType
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.training.infrastructure.repository.command.TrainingSchedulesCommandRepository
import io.runnershigh.backend.training.ui.dto.SaveTrainingSchedule
import io.runnershigh.backend.user.util.LoginUserContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZoneId

@Service
@Transactional
class TrainingSchedulesCommandUseCase(
    private val repository: TrainingSchedulesCommandRepository,
    private val loginUserContext: LoginUserContext,
) {
    private val MAX_FUTURE_DAYS = 365L

    fun createTrainingSchedule(dto: SaveTrainingSchedule): TrainingSchedules {
        // step01. 훈련일자 값 검증 -> 훈련일자가 현재보다 이전일 수 없음
        validateTrainingTime(dto.scheduledDate)

        return repository.save(dto.toEntity(loginUserContext.getCurrentUser()))
    }

    private fun validateTrainingTime(schedule: LocalDate) {
        val now = LocalDate.now(ZoneId.of("Asia/Seoul"))

        if (schedule.isBefore(now)) {
            throw TrainingException(TrainingExceptionType.CANNOT_REGISTER_PAST_TRAINING)
        }

        if (schedule.isAfter(now.plusDays(MAX_FUTURE_DAYS))) {
            throw TrainingException(TrainingExceptionType.CANNOT_REGISTER_TRAINING_BEYOND_ONE_YEAR)
        }
    }
}