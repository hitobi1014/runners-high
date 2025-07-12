package io.runnershigh.backend.training.extension

import io.runnershigh.backend.training.entity.TrainingPlanItems
import java.time.Duration
import java.time.LocalTime

/**
 * TrainingPlanItems 리스트에서 총 거리와 총 시간을 계산합니다.
 * @return Pair<Double, LocalTime> 형태로 총 거리와 총 시간을 반환
 */
fun List<TrainingPlanItems>.calculateTotalDistanceAndTime(): Pair<Double, Duration> {

    val (totalDistance, totalTime) = fold(Pair(0.0, Duration.ZERO)) { acc, item ->
        val distance = item.targetDistance
        val time = item.targetTime

        Pair(acc.first + distance, acc.second.plus(time))
    }

    return Pair(
        totalDistance,
        totalTime
    )
}