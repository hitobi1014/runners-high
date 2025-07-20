package io.runnershigh.backend.training.repository.querydsl

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import io.runnershigh.backend.training.entity.QTrainingSchedules.trainingSchedules
import io.runnershigh.backend.training.entity.TrainingSchedules
import io.runnershigh.backend.training.entity.TrainingStatus
import io.runnershigh.backend.user.entity.UserEntity
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class TrainingScheduleRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : TrainingScheduleRepositoryCustom {

    override fun retrieveTrainingSchedules(user: UserEntity): List<TrainingSchedules> {
        return queryFactory.selectFrom(trainingSchedules)
            .where(eqUser(user))
            .orderBy(trainingSchedules.scheduledDateTime.desc())
            .fetch()
    }

    override fun retrieveNextUpcomingSchedule(user: UserEntity): TrainingSchedules? {
        return queryFactory.selectFrom(trainingSchedules)
            .where(
                eqUser(user),
                trainingSchedules.scheduledDateTime.goe(LocalDateTime.now()),
            )
            .orderBy(trainingSchedules.scheduledDateTime.asc())
            .fetchFirst()
    }

    override fun findThisWeekTrainingSchedules(
        user: UserEntity,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        plannedOnly: Boolean,
    ): List<TrainingSchedules> {
        return queryFactory.selectFrom(trainingSchedules)
            .where(
                eqUser(user),
                betweenScheduleDate(startDateTime, endDateTime),
                if (plannedOnly) eqPlanStatus() else null
            )
            .orderBy(trainingSchedules.scheduledDateTime.desc())
            .fetch()
    }

    private fun betweenScheduleDate(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
    ): BooleanExpression? =
        trainingSchedules.scheduledDateTime.between(startDateTime, endDateTime)

    private fun eqUser(user: UserEntity): BooleanExpression? =
        trainingSchedules.user.eq(user)

    private fun eqPlanStatus(): BooleanExpression? =
        trainingSchedules.status.eq(TrainingStatus.PLANNED)
}