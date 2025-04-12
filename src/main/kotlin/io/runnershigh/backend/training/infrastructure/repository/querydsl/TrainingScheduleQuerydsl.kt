package io.runnershigh.backend.training.infrastructure.repository.querydsl

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import io.runnershigh.backend.training.infrastructure.entity.QTrainingSchedules.trainingSchedules
import io.runnershigh.backend.training.infrastructure.entity.TrainingSchedules
import io.runnershigh.backend.user.infrastructure.entity.UserEntity
import org.springframework.stereotype.Repository

@Repository
class TrainingScheduleQuerydsl(
    private val queryFactory: JPAQueryFactory,
) {

    fun findByUser(user: UserEntity): List<TrainingSchedules> {
        return queryFactory.selectFrom(trainingSchedules)
            .where(eqUser(user))
            .orderBy(trainingSchedules.scheduledDate.asc())
            .fetch()
    }

    private fun eqUser(user: UserEntity): BooleanExpression? =
        trainingSchedules.user.eq(user)
}