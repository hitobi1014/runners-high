package io.runnershigh.backend.training.extension

import io.runnershigh.backend.training.entity.TrainingPlanItems
import java.time.LocalTime

/**
 * TrainingPlanItems 리스트에서 총 거리와 총 시간을 계산합니다.
 * @return Pair<Double, LocalTime> 형태로 총 거리와 총 시간을 반환
 */
fun List<TrainingPlanItems>.calculateTotalDistanceAndTime(): Pair<Double, LocalTime> {

    val (totalDistance, totalTime) = fold(Pair(0.0, 0L)) { acc, item ->
        val distance = item.targetDistance ?: 0.0
        val second = item.targetTime?.toSecondOfDay()?.toLong() ?: 0L
        Pair(acc.first + distance, acc.second + second)
    }

    return Pair(
        totalDistance,
        LocalTime.ofSecondOfDay(totalTime)
    )
}