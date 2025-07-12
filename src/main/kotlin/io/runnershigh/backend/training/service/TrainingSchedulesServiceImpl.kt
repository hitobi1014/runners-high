package io.runnershigh.backend.training.service

import io.runnershigh.backend.shared.util.DateUtils
import io.runnershigh.backend.training.dto.request.SaveTrainingGroup
import io.runnershigh.backend.training.dto.request.SaveTrainingInfo
import io.runnershigh.backend.training.dto.request.SaveTrainingItem
import io.runnershigh.backend.training.dto.response.NextTrainingSchedule
import io.runnershigh.backend.training.dto.response.ReadTrainingSchedule
import io.runnershigh.backend.training.dto.response.SummaryThisWeekSchedule
import io.runnershigh.backend.training.dto.response.WeeklyTrainingSchedule
import io.runnershigh.backend.training.entity.TrainingPlanGroups
import io.runnershigh.backend.training.entity.TrainingPlanItems
import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.training.exception.TrainingException
import io.runnershigh.backend.training.exception.TrainingExceptionType
import io.runnershigh.backend.training.extension.calculateTotalDistanceAndTime
import io.runnershigh.backend.training.mapper.toReadTrainingSchedule
import io.runnershigh.backend.training.mapper.toWeeklyTrainingSchedule
import io.runnershigh.backend.training.repository.TrainingSchedulesRepository
import io.runnershigh.backend.user.entity.UserEntity
import io.runnershigh.backend.user.util.LoginUserContext
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId


@Service
@Transactional(readOnly = true)
class TrainingSchedulesServiceImpl(
    private val trainingSchedulesRepository: TrainingSchedulesRepository,
    private val loginUserContext: LoginUserContext,
) : TrainingSchedulesService {

    private val logger = KotlinLogging.logger {}

    companion object {
        private const val MAX_FUTURE_DAYS = 365L
        private const val MAX_TRAINING_DISTANCE = 100.0
    }

    @Transactional
    override fun createTrainingSchedule(dto: SaveTrainingInfo): TrainingSchedules {
        /*
        DTO 값 검증
        1. 권한 검증
            - 현재 로그인 유저 확인
        2. 데이터 정합성 검증
            - 훈련 등록 날짜가 최대 1년 이내인지
            - 그룹 순서가 연속적인지 (1,2,3 ...)
            - 아이템 순서가 연속적인지
            - 페이스 값들이 논리적으로 올바른지
        3. 비즈니스 룰 검증
            - 거리 제한 (최대 100km) -> 예상거리 검증
         */

        // #1. 권한 검증
        val loginUser = loginUserContext.getCurrentUser()

        // #2. DTO 값 검증 - 데이터 정합성
        validateTrainingTime(dto.scheduledDate)
        validateGroupOrder(dto.groups)
        validateItemOrder(dto.groups)
        validatePaceRange(dto.groups)
        validateTotalDistance(dto.groups)

        // #3. DTO → 엔티티 변환 및 저장
        val schedule = createTrainingScheduleEntity(dto, loginUser)

        return trainingSchedulesRepository.save(schedule)
    }

    override fun getTrainingSchedules(): List<ReadTrainingSchedule> {
        // #1. 현재 로그인 유저 가져오기
        val loginUser = loginUserContext.getCurrentUser()

        // #2. 유저로 등록된 훈련 일정 목록 가져오기
        val trainingSchedulesList = trainingSchedulesRepository.retrieveTrainingSchedules(loginUser)

        // #3. 엔티티 -> Schedule 변환
        return trainingSchedulesList.map(TrainingSchedules::toReadTrainingSchedule)
    }

    override fun getCurrentWeekTrainingSchedules(): List<WeeklyTrainingSchedule> {
        // #1. 현재 로그인 한 유저
        val loginUser = loginUserContext.getCurrentUser()

        // #2. 이번 주 훈련 일정 가져오기
        val (previousMonday, nextSunday) = DateUtils.getWeekBoundaries()
        val trainingSchedulesList =
            trainingSchedulesRepository.findThisWeekTrainingSchedules(
                user = loginUser,
                startDate = previousMonday,
                endDate = nextSunday
            )

        // #3. 엔티티 -> Schedule 변환
        return trainingSchedulesList.map(TrainingSchedules::toWeeklyTrainingSchedule)
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

    override fun getSummaryThisWeekForSchedule(): SummaryThisWeekSchedule {
        val loginUser = loginUserContext.getCurrentUser()

        val startDate = DateUtils.findPreviousMonday()
        val endDate = DateUtils.findNextSunday()

        // 필요 데이터 추출
        val weekTrainingSchedules =
            trainingSchedulesRepository.findThisWeekTrainingSchedules(
                loginUser,
                startDate,
                endDate,
                plannedOnly = true
            )
        val items = weekTrainingSchedules.flatMap { it.groups }.flatMap { it.items }

        // DTO 산출
        val scheduleCount = weekTrainingSchedules.size
        val totalDistance = items.sumOf { it.estimatedDistance }
        val totalTime = items.map { it.estimatedTime }
            .fold(Duration.ZERO) { acc, duration -> acc.plus(duration) }

        return SummaryThisWeekSchedule(
            scheduleCount = scheduleCount,
            totalDistance = totalDistance,
            totalTime = totalTime
        )
    }

    private fun validateTrainingTime(schedule: LocalDate) {
        val now = LocalDate.now(ZoneId.of("Asia/Seoul"))

        if (schedule.isAfter(now.plusDays(MAX_FUTURE_DAYS))) {
            throw TrainingException(TrainingExceptionType.CANNOT_REGISTER_TRAINING_BEYOND_ONE_YEAR)
        }
    }

    /**
     * 그룹 순서가 순차적으로 맞는지 검증
     * ex) 1 -> 2-> 3
     */
    private fun validateGroupOrder(groups: List<SaveTrainingGroup>) {
        val orders = groups.map { it.groupOrder }.sorted()
        val expectedOrders = (1..groups.size).toList()

        if (orders != expectedOrders) {
            throw TrainingException(TrainingExceptionType.INVALID_GROUP_ORDER)
        }
    }

    /**
     * 아이템 순서가 순차적으로 되어있는지 검증
     * ex) 1->2->3
     */
    private fun validateItemOrder(groups: List<SaveTrainingGroup>) {
        groups.forEach { group ->
            val itemOrders = group.items.map { it.itemOrder }.sorted()
            val expectedOrders = (1..group.items.size).toList()

            if (itemOrders != expectedOrders) {
                throw TrainingException(TrainingExceptionType.INVALID_ITEM_ORDER)
            }
        }
    }

    /**
     * 평균 페이스가 적정한 값으로 만들어졌는지 검증
     * Duration 값으로 비교했을때 정상 케이스
     * 최소 페이스 > 평균 페이스 && 평균 페이스 > 최대 페이스
     * ex) 최소페이스: PT9M / 최대페이스: PT4M / 평균페이스: PT6M30S
     */
    private fun validatePaceRange(groups: List<SaveTrainingGroup>) {
        groups.flatMap { it.items }.forEach { item ->
            val min = item.targetMinPace
            val avg = item.targetAvgPace
            val max = item.targetMaxPace

            if (max > avg || avg > min) {
                logger.info { "Invalid pace range: min: $min, avg: $avg, max: $max" }
                throw TrainingException(TrainingExceptionType.INVALID_PACE_RANGE)
            }
        }
    }

    private fun validateTotalDistance(groups: List<SaveTrainingGroup>) {
        val totalDistance = groups.flatMap { it.items }.sumOf { it.estimatedDistance }

        if (totalDistance > MAX_TRAINING_DISTANCE) {
            throw TrainingException(TrainingExceptionType.TRAINING_DISTANCE_LIMIT_EXCEEDED)
        }
    }

    /**
     * DTO를 TrainingSchedules 엔티티로 변환
     * Groups와 Items까지 함께 변환하여 Cascade로 한번에 저장
     */
    private fun createTrainingScheduleEntity(
        dto: SaveTrainingInfo,
        user: UserEntity,
    ): TrainingSchedules {
        val schedule = TrainingSchedules(
            user = user,
            title = dto.title,
            location = dto.location,
            scheduledDate = dto.scheduledDate,
            description = dto.description,
            status = dto.status,
            color = dto.color
        )

        // Groups 변환 및 연관관계 설정
        dto.groups.forEach { groupDto ->
            val group = createTrainingGroupEntity(groupDto, schedule)
            schedule.groups.add(group)
        }

        return schedule
    }

    /**
     * SaveTrainingGroup DTO를 TrainingPlanGroups 엔티티로 변환
     */
    private fun createTrainingGroupEntity(
        dto: SaveTrainingGroup,
        schedule: TrainingSchedules,
    ): TrainingPlanGroups {
        val group = TrainingPlanGroups(
            schedule = schedule,
            groupOrder = dto.groupOrder,
            repeatCount = dto.repeatCount,
            description = dto.description
        )

        // Items 변환 및 연관관계 설정
        dto.items.forEach { itemDto ->
            val item = createTrainingItemEntity(itemDto, group)
            group.items.add(item)
        }

        return group
    }

    /**
     * SaveTrainingItem DTO를 TrainingPlanItems 엔티티로 변환
     */
    private fun createTrainingItemEntity(
        dto: SaveTrainingItem,
        group: TrainingPlanGroups,
    ): TrainingPlanItems {
        return TrainingPlanItems(
            group = group,
            itemOrder = dto.itemOrder,
            targetType = dto.targetType,
            targetMinPace = dto.targetMinPace,
            targetMaxPace = dto.targetMaxPace,
            targetAvgPace = dto.targetAvgPace,
            runningTypeCode = dto.runningTypeCode,
            distanceUnit = dto.distanceUnit,
            targetDistance = dto.targetDistance,
            targetTime = dto.targetTime,
            estimatedDistance = dto.estimatedDistance,
            estimatedTime = dto.estimatedTime,
            note = dto.note
        )
    }
}