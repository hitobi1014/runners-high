package io.runnershigh.backend.training.ui

import io.runnershigh.backend.shared.response.ApiResponse
import io.runnershigh.backend.shared.response.CommonResponseMessage
import io.runnershigh.backend.shared.response.ResponseUtils
import io.runnershigh.backend.training.application.TrainingSchedulesUseCase
import io.runnershigh.backend.training.ui.dto.response.ReadTrainingSchedule
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/training-schedule")
class TrainingScheduleController(
    private val trainingSchedulesUseCase: TrainingSchedulesUseCase,
) {

    private val logger = KotlinLogging.logger {}

    //    훈련 일정 전체 조회
    @GetMapping
    fun getTrainingSchedules(): ResponseEntity<ApiResponse<List<ReadTrainingSchedule>>> {
        logger.info { "훈련 일정 전체 조회 controller" }
        val result = trainingSchedulesUseCase.getTrainingSchedules()
        return ResponseUtils.success(result, CommonResponseMessage.SUCCESS_GET_DATA.message)
    }

    //    이번 주 훈련 일정 조회
    @GetMapping("/current-week")
    fun getCurrentTrainingSchedules(): ResponseEntity<ApiResponse<List<ReadTrainingSchedule>>> {
        val result = trainingSchedulesUseCase.getCurrentWeekTrainingSchedules()
        return ResponseUtils.success(result, CommonResponseMessage.SUCCESS_GET_DATA.message)
    }

    //    다음 훈련 일정 조회
    @GetMapping("/next")
    fun getNextTrainingSchedule(): ResponseEntity<ApiResponse<ReadTrainingSchedule?>> {
        val result = trainingSchedulesUseCase.getNextUpcomingTrainingSchedule()
        return ResponseUtils.success(result, CommonResponseMessage.SUCCESS_GET_DATA.message)
    }
}