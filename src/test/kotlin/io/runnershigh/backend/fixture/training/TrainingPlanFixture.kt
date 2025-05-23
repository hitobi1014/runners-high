package io.runnershigh.backend.fixture.training

import io.runnershigh.backend.fixture.util.randomLocalTime
import io.runnershigh.backend.training.dto.request.SaveTrainingPlanItem
import io.runnershigh.backend.training.entity.enum.DistanceUnit
import io.runnershigh.backend.training.entity.enum.TargetType
import net.datafaker.Faker
import java.time.LocalTime
import java.util.*

object TrainingPlanFixture {
    private val faker = Faker(Locale.KOREA)

    fun createSaveTrainingPlanItemDto(
        itemOrder: Int = faker.number().numberBetween(1, 9),
        targetType: TargetType = TargetType.DISTANCE,
        targetMinPace: LocalTime = faker.randomLocalTime(maxMinutes = 8),
        targetMaxPace: LocalTime = faker.randomLocalTime(),
        targetAvgPace: LocalTime = faker.randomLocalTime(),
        runningTypeCode: Int = 101,
        distanceUnit: DistanceUnit = DistanceUnit.KILOMETER,
        targetDistance: Double? = faker.number().randomDouble(1, 5, 30),
        targetTime: LocalTime? = faker.randomLocalTime(),
        estimatedDistance: Double? = faker.number().randomDouble(1, 5, 30),
        estimatedTime: LocalTime? = faker.randomLocalTime(minMinutes = 25, maxMinutes = 59),
        note: String? = faker.lorem().word(),
    ) = SaveTrainingPlanItem(
        itemOrder = itemOrder,
        targetType = targetType,
        targetMinPace = targetMinPace,
        targetMaxPace = targetMaxPace,
        targetAvgPace = targetAvgPace,
        runningTypeCode = runningTypeCode,
        distanceUnit = distanceUnit,
        targetDistance = targetDistance,
        targetTime = targetTime,
        estimatedDistance = estimatedDistance,
        estimatedTime = estimatedTime,
        note = note,
    )
}