package io.runnershigh.backend.training.application.command

import io.runnershigh.backend.shared.util.DateUtils
import io.runnershigh.backend.training.domain.mapper.toDto
import io.runnershigh.backend.training.domain.mapper.toEntity
import io.runnershigh.backend.training.exception.TrainingException
import io.runnershigh.backend.training.exception.TrainingExceptionType
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.training.infrastructure.repository.TrainingSchedulesRepository
import io.runnershigh.backend.training.infrastructure.repository.querydsl.TrainingScheduleQuerydsl
import io.runnershigh.backend.training.ui.dto.request.SaveTrainingSchedule
import io.runnershigh.backend.training.ui.dto.response.ReadTrainingSchedule
import io.runnershigh.backend.user.util.LoginUserContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZoneId

@Service
@Transactional
class TrainingSchedulesCommandUseCase(
    private val trainingSchedulesRepository: TrainingSchedulesRepository,
    private val trainingScheduleQuerydsl: TrainingScheduleQuerydsl,
    private val loginUserContext: LoginUserContext,
) {
    private val MAX_FUTURE_DAYS = 365L

    fun createTrainingSchedule(dto: SaveTrainingSchedule): TrainingSchedules {
        // step01. 훈련일자 값 검증 -> 훈련일자가 현재보다 이전일 수 없음
        validateTrainingTime(dto.scheduledDate)

        return trainingSchedulesRepository.save(dto.toEntity(loginUserContext.getCurrentUser()))
    }

    fun getTrainingSchedules(date: LocalDate): List<ReadTrainingSchedule> {
        // #1. 현재 로그인 유저 가져오기
        val loginUser = loginUserContext.getCurrentUser()

        // #2. 유저로 등록된 훈련 일정 목록 가져오기
        val trainingSchedulesList = trainingScheduleQuerydsl.findByUser(loginUser)

        // #3. 엔티티 -> Schedule 변환
        return trainingSchedulesList.map(TrainingSchedules::toDto)
    }

    fun getCurrentWeekTrainingSchedules(): List<ReadTrainingSchedule> {
        // #1. 현재 로그인 한 유저
        val loginUser = loginUserContext.getCurrentUser()

        // #2. 이번 주 훈련 일정 가져오기
        val (previousSunday, nextSaturday) = DateUtils.getWeekBoundaries(LocalDate.now())
        val trainingSchedulesList =
            trainingScheduleQuerydsl.findCurrentWeekSchedulesByUser(
                user = loginUser,
                previousSunday = previousSunday,
                nextSaturday = nextSaturday
            )

        // #3. 엔티티 -> Schedule 변환
        return trainingSchedulesList.map(TrainingSchedules::toDto)
    }

    fun getNextUpcomingTrainingSchedule(): ReadTrainingSchedule? {
        val loginUser = loginUserContext.getCurrentUser()
        val trainingSchedule = trainingScheduleQuerydsl.findNextUpcomingScheduleByUser(loginUser)
        return trainingSchedule?.toDto()
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