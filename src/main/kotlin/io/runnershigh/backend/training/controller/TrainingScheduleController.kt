package io.runnershigh.backend.training.controller

import io.runnershigh.backend.shared.response.ApiResponse
import io.runnershigh.backend.shared.response.CommonResponseMessage
import io.runnershigh.backend.shared.response.ResponseUtils
import io.runnershigh.backend.training.dto.request.SaveTrainingInfo
import io.runnershigh.backend.training.dto.response.NextTrainingSchedule
import io.runnershigh.backend.training.dto.response.ReadTrainingSchedule
import io.runnershigh.backend.training.dto.response.SummaryThisWeekSchedule
import io.runnershigh.backend.training.dto.response.WeeklyTrainingSchedule
import io.runnershigh.backend.training.service.TrainingSchedulesService
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/training-schedule")
class TrainingScheduleController(
    private val trainingSchedulesService: TrainingSchedulesService,
) {

    private val logger = KotlinLogging.logger {}

    //    훈련 일정 전체 조회
    @GetMapping
    fun getTrainingSchedules(): ResponseEntity<ApiResponse<List<ReadTrainingSchedule>>> {
        logger.info { "훈련 일정 전체 조회 controller" }
        val result = trainingSchedulesService.getTrainingSchedules()
        return ResponseUtils.success(result, CommonResponseMessage.SUCCESS_GET_DATA.message)
    }

    //    이번 주 훈련 일정 조회
    @GetMapping("/current-week")
    fun getThisWeekTrainingSchedules(): ResponseEntity<ApiResponse<List<WeeklyTrainingSchedule>>> {
        val result = trainingSchedulesService.getThisWeekTrainingSchedules()
        return ResponseUtils.success(result, CommonResponseMessage.SUCCESS_GET_DATA.message)
    }

    //    다음 훈련 일정 조회
    @GetMapping("/next")
    fun getNextTrainingSchedule(): ResponseEntity<ApiResponse<NextTrainingSchedule?>> {
        val result = trainingSchedulesService.getNextUpcomingTrainingSchedule()
        return ResponseUtils.success(result, CommonResponseMessage.SUCCESS_GET_DATA.message)
    }

    //    훈련 일정 저장
    @PostMapping
    fun saveTrainingSchedule(
        @Valid @RequestBody dto: SaveTrainingInfo,
    ): ResponseEntity<ApiResponse<Unit>> {
        logger.info { "훈련 일정 저장 controller - title: ${dto.title}" }
        trainingSchedulesService.createTrainingSchedule(dto)
        return ResponseUtils.success(Unit, CommonResponseMessage.SUCCESS_CREATE_DATA.message)
    }

    // 이번 주 훈련 요약 조회
    @GetMapping("/current-week/summary")
    fun getCurrentWeekTrainingScheduleSummary(): ResponseEntity<ApiResponse<SummaryThisWeekSchedule>> {
        val result = trainingSchedulesService.getSummaryThisWeekForSchedule()
        return ResponseUtils.success(result, CommonResponseMessage.SUCCESS_GET_DATA.message)
    }

}