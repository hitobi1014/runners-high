package io.runnershigh.backend.shared.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class QuerydslTestConfig {
    /**
     * Creates a Querydsl JPAQueryFactory bean for constructing type-safe JPA queries.
     *
     * This method initializes the factory with the provided EntityManager, enabling its use in tests.
     *
     * @return a new JPAQueryFactory instance configured with the given EntityManager.
     */
    @Bean
    fun jpaQueryFactory(entityManager: EntityManager): JPAQueryFactory {
        return JPAQueryFactory(entityManager)
    }
}