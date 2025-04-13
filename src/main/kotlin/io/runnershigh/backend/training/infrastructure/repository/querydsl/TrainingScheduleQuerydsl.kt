package io.runnershigh.backend.training.infrastructure.repository.querydsl

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import io.runnershigh.backend.training.domain.enum.TrainingStatus
import io.runnershigh.backend.training.infrastructure.entity.QTrainingSchedules.trainingSchedules
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.user.infrastructure.entity.UserEntity
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class TrainingScheduleQuerydsl(
    private val queryFactory: JPAQueryFactory,
) {

    fun findByUser(user: UserEntity): List<TrainingSchedules> {
        return queryFactory.selectFrom(trainingSchedules)
            .where(eqUser(user))
            .orderBy(trainingSchedules.scheduledDate.desc())
            .fetch()
    }

    fun findCurrentWeekSchedulesByUser(
        user: UserEntity,
        previousSunday: LocalDate,
        nextSaturday: LocalDate,
    ): List<TrainingSchedules> {
        return queryFactory.selectFrom(trainingSchedules)
            .where(
                eqUser(user),
                betweenSundayAndSaturday(previousSunday, nextSaturday)
            )
            .orderBy(trainingSchedules.scheduledDate.desc())
            .fetch()
    }

    fun findNextUpcomingScheduleByUser(user: UserEntity): TrainingSchedules? {
        return queryFactory.selectFrom(trainingSchedules)
            .where(
                eqUser(user),
                trainingSchedules.scheduledDate.goe(LocalDate.now()),
            )
            .orderBy(trainingSchedules.scheduledDate.asc())
            .fetchFirst()
    }


    private fun betweenSundayAndSaturday(
        beforeSunday: LocalDate,
        afterSaturday: LocalDate,
    ): BooleanExpression? =
        trainingSchedules.scheduledDate.between(beforeSunday, afterSaturday)

    private fun eqUser(user: UserEntity): BooleanExpression? =
        trainingSchedules.user.eq(user)
}