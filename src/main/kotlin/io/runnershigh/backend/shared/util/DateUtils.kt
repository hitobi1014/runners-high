package io.runnershigh.backend.shared.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

object DateUtils {


    /**
     * Returns the most recent Sunday relative to the provided date.
     *
     * If the given date is a Sunday, that date is returned; otherwise, the previous Sunday is computed.
     *
     * @param date the base date to evaluate (defaults to the current date)
     * @return the date corresponding to the most recent Sunday
     */
    fun findPreviousSunday(date: LocalDate = LocalDate.now()): LocalDate {
        return if (date.dayOfWeek == DayOfWeek.SUNDAY) {
            date
        } else {
            date.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
        }
    }


    /**
     * Returns the next Saturday based on the given date.
     *
     * If the specified date is already a Saturday, the same date is returned.
     * Otherwise, the function computes the upcoming Saturday from the provided date.
     *
     * @param date The reference date, defaulting to the current date.
     * @return The date of the next Saturday relative to the given date.
     */
    fun findNextSaturday(date: LocalDate = LocalDate.now()): LocalDate {
        return if (date.dayOfWeek == DayOfWeek.SATURDAY) {
            date
        } else {
            date.with(TemporalAdjusters.next(DayOfWeek.SATURDAY))
        }
    }


    /**
     * Returns the week boundaries based on the provided date or the current date.
     *
     * The boundaries are defined as the closest previous Sunday and the next Saturday.
     *
     * @param date the reference date (defaults to the current date)
     * @return a pair where the first element is the previous Sunday and the second element is the next Saturday
     */
    fun getWeekBoundaries(date: LocalDate = LocalDate.now()): Pair<LocalDate, LocalDate> {
        val previousSunday = findPreviousSunday(date)
        val nextSaturday = findNextSaturday(date)
        return previousSunday to nextSaturday
    }
}