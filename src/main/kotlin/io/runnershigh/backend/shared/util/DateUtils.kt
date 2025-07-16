package io.runnershigh.backend.shared.util

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

object DateUtils {
    /**
     * 주어진 날짜 또는 현재 날짜를 기준으로 가장 가까운 이전 월요일 날짜를 반환합니다.
     * 현재 날짜가 월요일인 경우 현재 날짜를 반환합니다.
     *
     * @param dateTime 기준 날짜 (기본값: 현재 날짜)
     * @return 가장 가까운 이전 월요일 날짜
     */
    fun findPreviousMonday(dateTime: LocalDateTime = LocalDateTime.now()): LocalDateTime {
        return if (dateTime.dayOfWeek == DayOfWeek.MONDAY) {
            dateTime
        } else {
            dateTime.with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
        }
    }

    fun findNextSunday(dateTime: LocalDateTime = LocalDateTime.now()): LocalDateTime {
        return if (dateTime.dayOfWeek == DayOfWeek.SUNDAY) {
            dateTime
        } else {
            dateTime.with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
        }
    }

    /**
     * 주어진 날짜 또는 현재 날짜를 기준으로 해당 주의 경계를 반환합니다.
     *
     * 경계는 가장 가까운 이전 월요일과 이후 일요일로 정의됩니다.
     *
     * @param dateTime 기준 날짜 (기본값: 현재 날짜)
     * @return 해당 주의 경계를 나타내는 Pair (이전 월요일, 이후 일요일)
     */
    fun getWeekBoundaries(dateTime: LocalDateTime = LocalDateTime.now()): Pair<LocalDateTime, LocalDateTime> {
        val previousMonday = findPreviousMonday(dateTime)
        val nextSunday = findNextSunday(dateTime)
        return previousMonday to nextSunday
    }
}