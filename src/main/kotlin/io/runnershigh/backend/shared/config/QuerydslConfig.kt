package io.runnershigh.backend.shared.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QuerydslConfig(
    @PersistenceContext
    private val em: EntityManager,
) {
    @Bean
    fun querydsl(): JPAQueryFactory = JPAQueryFactory(em)
}