package io.runnershigh.backend.fixture.util

import net.datafaker.Faker
import java.time.LocalTime

fun Faker.randomLocalTime(minMinutes: Int = 1, maxMinutes: Int = 10): LocalTime {
    val randomMinutes = this.number().numberBetween(minMinutes, maxMinutes + 1)
    val randomSeconds = this.number().numberBetween(0, 59)
    return LocalTime.of(0, randomMinutes, randomSeconds)
}