package io.runnershigh.backend.shared.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DateUtilsTest {

    @Test
    @DisplayName("현재 일자 기준으로 가장 가까운 이전 월요일, 이후 일요일 반환")
    fun findPreviousMondayAndNextSunday_whenCurrentDate_returnsMondayAndSunday() {
        //given
        val date = LocalDate.of(2025, 7, 12)
        val expectedMonday = LocalDate.of(2025, 7, 7)
        val expectedSunday = LocalDate.of(2025, 7, 13)

        //when
        val result: Pair<LocalDate, LocalDate> = DateUtils.getWeekBoundaries(date)

        //then
        assertEquals(expectedMonday, result.first)
        assertEquals(expectedSunday, result.second)
    }
}