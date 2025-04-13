package io.runnershigh.backend.training.infrastructure.repository.querydsl

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import io.runnershigh.backend.training.infrastructure.entity.QTrainingSchedules.trainingSchedules
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.user.infrastructure.entity.UserEntity
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class TrainingScheduleQuerydsl(
    private val queryFactory: JPAQueryFactory,
) {

    /**
     * Retrieves all training schedules associated with the specified user,
     * ordered by scheduled date in descending order.
     *
     * @param user the user entity to filter training schedules for.
     * @return a list of training schedules linked to the provided user.
     */
    fun findByUser(user: UserEntity): List<TrainingSchedules> {
        return queryFactory.selectFrom(trainingSchedules)
            .where(eqUser(user))
            .orderBy(trainingSchedules.scheduledDate.desc())
            .fetch()
    }

    /**
     * Retrieves the training schedules for the current week for a specified user.
     *
     * This method fetches training schedules with dates between the provided previous Sunday and next Saturday,
     * ordering them by scheduled date in descending order.
     *
     * @param user the user whose weekly training schedules are being queried
     * @param previousSunday the start date of the week (typically the previous Sunday)
     * @param nextSaturday the end date of the week (typically the next Saturday)
     * @return a list of training schedules falling within the specified week
     */
    fun findCurrentWeekSchedulesByUser(
        user: UserEntity,
        previousSunday: LocalDate,
        nextSaturday: LocalDate,
    ): List<TrainingSchedules> {
        return queryFactory.selectFrom(trainingSchedules)
            .where(betweenSundayAndSaturday(previousSunday, nextSaturday))
            .orderBy(trainingSchedules.scheduledDate.desc())
            .fetch()
    }

    /**
     * Retrieves the next upcoming training schedule for the specified user.
     *
     * This function queries the training schedules by filtering for the given user, ordering the results by scheduled date in ascending order, and returning the first matching schedule. If no upcoming schedule is found, it returns null.
     *
     * @param user the user entity for whom to find the next training schedule.
     * @return the next scheduled training session or null if none exists.
     */
    fun findNextUpcomingScheduleByUser(user: UserEntity): TrainingSchedules? {
        return queryFactory.selectFrom(trainingSchedules)
            .where(eqUser(user))
            .orderBy(trainingSchedules.scheduledDate.asc())
            .fetchFirst()
    }

    /**
         * Builds a Boolean expression that checks whether the training schedule's scheduled date
         * falls within the specified range.
         *
         * @param beforeSunday The start date of the interval (inclusive).
         * @param afterSaturday The end date of the interval (inclusive).
         * @return A BooleanExpression representing the condition for dates between beforeSunday and afterSaturday.
         */
        private fun betweenSundayAndSaturday(
        beforeSunday: LocalDate,
        afterSaturday: LocalDate,
    ): BooleanExpression? =
        trainingSchedules.scheduledDate.between(beforeSunday, afterSaturday)

    /**
         * Generates a Boolean expression to filter training schedules by the specified user.
         *
         * @param user the user for which to match training schedules.
         * @return a BooleanExpression that evaluates to true when the training schedule's user matches the provided user.
         */
        private fun eqUser(user: UserEntity): BooleanExpression? =
        trainingSchedules.user.eq(user)
}