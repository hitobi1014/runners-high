package io.runnershigh.backend.training.service

import io.runnershigh.backend.shared.util.DateUtils
import io.runnershigh.backend.training.dto.request.SaveTrainingInfo
import io.runnershigh.backend.training.dto.response.NextTrainingSchedule
import io.runnershigh.backend.training.dto.response.ReadTrainingSchedule
import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.training.exception.TrainingException
import io.runnershigh.backend.training.exception.TrainingExceptionType
import io.runnershigh.backend.training.extension.calculateTotalDistanceAndTime
import io.runnershigh.backend.training.mapper.toDto
import io.runnershigh.backend.training.mapper.toEntity
import io.runnershigh.backend.training.repository.TrainingSchedulesRepository
import io.runnershigh.backend.user.util.LoginUserContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZoneId


@Service
@Transactional
class TrainingSchedulesServiceImpl(
    private val trainingSchedulesRepository: TrainingSchedulesRepository,
    private val loginUserContext: LoginUserContext,
) : TrainingSchedulesService {

    companion object {
        private const val MAX_FUTURE_DAYS = 365L
    }

    override fun createTrainingSchedule(dto: SaveTrainingInfo): TrainingSchedules {
        // step01. 훈련일자 값 검증 -> 훈련일자가 현재보다 이전일 수 없음
        validateTrainingTime(dto.scheduledDate)

        return trainingSchedulesRepository.save(dto.toEntity(loginUserContext.getCurrentUser()))
    }

    override fun getTrainingSchedules(): List<ReadTrainingSchedule> {
        // #1. 현재 로그인 유저 가져오기
        val loginUser = loginUserContext.getCurrentUser()

        // #2. 유저로 등록된 훈련 일정 목록 가져오기
        val trainingSchedulesList = trainingSchedulesRepository.retrieveTrainingSchedules(loginUser)

        // #3. 엔티티 -> Schedule 변환
        return trainingSchedulesList.map(TrainingSchedules::toDto)
    }

    override fun getCurrentWeekTrainingSchedules(): List<ReadTrainingSchedule> {
        // #1. 현재 로그인 한 유저
        val loginUser = loginUserContext.getCurrentUser()

        // #2. 이번 주 훈련 일정 가져오기
        val (previousSunday, nextSaturday) = DateUtils.getWeekBoundaries(LocalDate.now())
        val trainingSchedulesList =
            trainingSchedulesRepository.retrieveCurrentWeekSchedules(
                user = loginUser,
                previousSunday = previousSunday,
                nextSaturday = nextSaturday
            )

        // #3. 엔티티 -> Schedule 변환
        return trainingSchedulesList.map(TrainingSchedules::toDto)
    }

    override fun getNextUpcomingTrainingSchedule(): NextTrainingSchedule? {
        // #1. 현재 로그인 유저 가져오기
        val loginUser = loginUserContext.getCurrentUser()

        // #2. 다음 예정된 훈련 일정 가져오기
        val trainingSchedule = trainingSchedulesRepository.retrieveNextUpcomingSchedule(loginUser)
            ?: return null

        // #3. 엔티티 -> Schedule 변환

        val allItems = trainingSchedule.groups.flatMap { it.items }
        val (totalDistance, totalTime) = allItems.calculateTotalDistanceAndTime()

        return NextTrainingSchedule(
            scheduleId = trainingSchedule.id,
            title = trainingSchedule.title,
            scheduledDate = trainingSchedule.scheduledDate,
            totalDistance = totalDistance,
            totalTime = totalTime
        )
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