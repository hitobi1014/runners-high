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

    /**
     * Creates a new training schedule after validating the scheduled date.
     *
     * The function first ensures that the scheduled date in the provided DTO is not in the past. If the validation
     * passes, it converts the DTO to an entity using the current user's context and saves it to the training schedules repository.
     *
     * @param dto data transfer object containing the training schedule details.
     * @return the newly created training schedule entity.
     */
    fun createTrainingSchedule(dto: SaveTrainingSchedule): TrainingSchedules {
        // step01. 훈련일자 값 검증 -> 훈련일자가 현재보다 이전일 수 없음
        validateTrainingTime(dto.scheduledDate)

        return trainingSchedulesRepository.save(dto.toEntity(loginUserContext.getCurrentUser()))
    }

    /**
     * Retrieves training schedules for the current user.
     *
     * This function obtains the currently logged-in user, fetches all training schedule entities associated with that user, and converts them into DTO representations.
     *
     * @param date a date value that currently does not influence the schedule filtering.
     * @return a list of [ReadTrainingSchedule] objects representing the user's training schedules.
     */
    fun getTrainingSchedules(date: LocalDate): List<ReadTrainingSchedule> {
        // #1. 현재 로그인 유저 가져오기
        val loginUser = loginUserContext.getCurrentUser()

        // #2. 유저로 등록된 훈련 일정 목록 가져오기
        val trainingSchedulesList = trainingScheduleQuerydsl.findByUser(loginUser)

        // #3. 엔티티 -> Schedule 변환
        return trainingSchedulesList.map(TrainingSchedules::toDto)
    }

    /**
     * Retrieves the training schedules for the current week for the currently logged-in user.
     *
     * This function calculates the current week's boundaries (from the previous Sunday to the next Saturday)
     * and fetches the user's training schedules within that period. The resulting schedule entities are then
     * converted into their DTO representations.
     *
     * @return a list of [ReadTrainingSchedule] objects for the current week.
     */
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

    /**
     * Retrieves the next upcoming training schedule for the authenticated user.
     *
     * This function fetches the currently logged-in user and queries for their next upcoming training schedule.
     * If a schedule is found, it is converted to its corresponding DTO; otherwise, null is returned.
     *
     * @return the DTO representing the next upcoming training schedule, or null if none exists.
     */
    fun getNextUpcomingTrainingSchedule(): ReadTrainingSchedule? {
        val loginUser = loginUserContext.getCurrentUser()
        val trainingSchedule = trainingScheduleQuerydsl.findNextUpcomingScheduleByUser(loginUser)
        return trainingSchedule?.toDto()
    }

    /**
     * Validates that the training schedule date falls within the allowed timeframe.
     *
     * This function ensures the provided schedule is not in the past relative to the current date in the Asia/Seoul timezone
     * and does not exceed the maximum allowed future date defined by MAX_FUTURE_DAYS (typically one year). If either condition
     * is violated, a TrainingException is thrown.
     *
     * @param schedule the training schedule date to validate.
     * @throws TrainingException if the schedule is in the past or beyond the permitted future limit.
     */
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