package io.runnershigh.backend.shared.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

object DateUtils {


    /**
     * 주어진 날짜 또는 현재 날짜를 기준으로 가장 가까운 이전 일요일 날짜를 반환합니다.
     *
     * @param date 기준 날짜 (기본값: 현재 날짜)
     * @return 가장 가까운 이전 일요일 날짜
     */
    fun findPreviousSunday(date: LocalDate = LocalDate.now()): LocalDate {
        return if (date.dayOfWeek == DayOfWeek.SUNDAY) {
            date
        } else {
            date.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
        }
    }


    /**
     * 주어진 날짜 또는 현재 날짜를 기준으로 가장 가까운 이후 토요일 날짜를 반환합니다.
     *
     * @param date 기준 날짜 (기본값: 현재 날짜)
     * @return 가장 가까운 이후 토요일 날짜
     */
    fun findNextSaturday(date: LocalDate = LocalDate.now()): LocalDate {
        return if (date.dayOfWeek == DayOfWeek.SATURDAY) {
            date
        } else {
            date.with(TemporalAdjusters.next(DayOfWeek.SATURDAY))
        }
    }


    /**
     * 주어진 날짜 또는 현재 날짜를 기준으로 해당 주의 경계를 반환합니다.
     *
     * 경계는 가장 가까운 이전 일요일과 이후 토요일로 정의됩니다.
     *
     * @param date 기준 날짜 (기본값: 현재 날짜)
     * @return 해당 주의 경계를 나타내는 Pair (이전 일요일, 이후 토요일)
     */
    fun getWeekBoundaries(date: LocalDate = LocalDate.now()): Pair<LocalDate, LocalDate> {
        val previousSunday = findPreviousSunday(date)
        val nextSaturday = findNextSaturday(date)
        return previousSunday to nextSaturday
    }
}