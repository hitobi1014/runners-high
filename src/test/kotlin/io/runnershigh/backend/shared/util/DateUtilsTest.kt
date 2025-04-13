package io.runnershigh.backend.shared.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DateUtilsTest {

    @Test
    @DisplayName("현재 날짜가 일요일인 경우 해당 날짜 반환")
    fun findPreviousSunday_whenCurrendDateIsSunday_returnSameDay() {
        //given
        val sunday = LocalDate.of(2025, 4, 13)

        //when
        val result = DateUtils.findPreviousSunday(sunday)

        //then
        assertEquals(sunday, result)
    }

    @Test
    @DisplayName("현재 날짜가 일요일이 아닌 경우 이전 일요일 반환")
    fun findPreviousSunday_whenCurrentDateIsNotSunday_returnsPreviousSunday() {
        //given
        val wednesday = LocalDate.of(2025, 4, 9)
        val expectedSunday = LocalDate.of(2025, 4, 6)

        //when
        val result = DateUtils.findPreviousSunday(wednesday)

        //then
        assertEquals(expectedSunday, result)
    }

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