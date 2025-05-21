package io.runnershigh.backend.training.repository.querydsl

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import io.runnershigh.backend.training.entity.QTrainingSchedules.trainingSchedules
import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.user.infrastructure.entity.UserEntity
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class TrainingScheduleRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : TrainingScheduleRepositoryCustom {

    override fun retrieveTrainingSchedules(user: UserEntity): List<TrainingSchedules> {
        return queryFactory.selectFrom(trainingSchedules)
            .where(eqUser(user))
            .orderBy(trainingSchedules.scheduledDate.desc())
            .fetch()
    }

    override fun retrieveCurrentWeekSchedules(
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

    override fun retrieveNextUpcomingSchedule(user: UserEntity): TrainingSchedules? {
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