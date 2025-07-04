package io.runnershigh.backend.training.service

import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.training.exception.TrainingException
import io.runnershigh.backend.training.exception.TrainingExceptionType
import io.runnershigh.backend.training.repository.TrainingPlanGroupRepository
import io.runnershigh.backend.training.repository.TrainingSchedulesRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class TrainingPlanGroupServiceImpl(
    private val trainingPlanGroupRepository: TrainingPlanGroupRepository,
    private val trainingSchedulesRepository: TrainingSchedulesRepository,
) : TrainingPlanGroupService {

//    /**
//     * 주어진 `SaveTrainingPlanGroup` DTO와 `scheduleId`를 기반으로 새로운 훈련 계획 그룹을 생성합니다.
//     *
//     * @param dto 훈련 계획 그룹을 저장하기 위한 데이터 전송 객체입니다.
//     * @param scheduleId 훈련 일정의 식별자(ID)입니다.
//     *
//     * @return 저장된 `TrainingPlanGroups` 엔티티.
//     *
//     * @throws TrainingException 주어진 `scheduleId`에 해당하는 훈련 일정이 존재하지 않을 경우 예외를 발생시킵니다.
//     */
//    override fun createTrainingPlanGroup(
//        dto: SaveTrainingPlanGroup,
//        scheduleId: Long,
//    ): TrainingPlanGroups {
//        val scheduleEntity = getTrainingSchedule(scheduleId)
//        val trainingPlanGroupEntity = dto.toEntity(scheduleEntity)
//
//        return trainingPlanGroupRepository.save(trainingPlanGroupEntity)
//    }

    private fun getTrainingSchedule(id: Long): TrainingSchedules {
        return trainingSchedulesRepository.findByIdOrNull(id) ?: throw TrainingException(
            TrainingExceptionType.CANNOT_FOUND_TRAINING_SCHEDULE
        )
    }

}