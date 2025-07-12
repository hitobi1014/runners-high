package io.runnershigh.backend.shared.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DateUtilsTest {

    @Test
    @DisplayName("현재 일자 기준으로 가장 가까운 이전 일요일, 가장 가까운 이후 토요일 반환")
    fun findPreviousSundayAndNextSaturday_whenCurrentDate_returnsSundayAndSaturday() {
        //given
        val date = LocalDate.of(2025, 4, 13)
        val expectedSunday = LocalDate.of(2025, 4, 13)
        val expectedSaturday = LocalDate.of(2025, 4, 19)

        //when
        val result: Pair<LocalDate, LocalDate> = DateUtils.getWeekBoundaries(date)

        //then
        assertEquals(expectedSunday, result.first)
        assertEquals(expectedSaturday, result.second)
    }
}