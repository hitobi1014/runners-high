package io.runnershigh.backend.user.repository.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import io.runnershigh.backend.user.entity.QUserEntity.userEntity
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : UserRepositoryCustom {

    override fun existsByLoginId(loginId: String): Boolean {
        return queryFactory.selectOne()
            .from(userEntity)
            .where(userEntity.loginId.eq(loginId))
            .fetchFirst() != null
    }

    override fun existsByNickname(nickname: String): Boolean {
        return queryFactory.selectOne()
            .from(userEntity)
            .where(userEntity.nickname.eq(nickname))
            .fetchFirst() != null
    }
}