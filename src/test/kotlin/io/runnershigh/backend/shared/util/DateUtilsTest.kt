package io.runnershigh.backend.shared.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DateUtilsTest {

    @Test
    @DisplayName("현재 일자 기준으로 가장 가까운 이전 월요일, 이후 일요일 반환")
    fun findPreviousMondayAndNextSunday_whenCurrentDate_returnsMondayAndSunday() {
        //given
        val nowTime = LocalTime.now()
        val date = LocalDateTime.of(LocalDate.of(2025, 7, 12), nowTime)
        val expectedMonday = LocalDateTime.of(LocalDate.of(2025, 7, 7), nowTime)
        val expectedSunday = LocalDateTime.of(LocalDate.of(2025, 7, 13), nowTime)

        //when
        val result: Pair<LocalDateTime, LocalDateTime> = DateUtils.getWeekBoundaries(date)

        //then
        assertEquals(expectedMonday, result.first)
        assertEquals(expectedSunday, result.second)
    }
}