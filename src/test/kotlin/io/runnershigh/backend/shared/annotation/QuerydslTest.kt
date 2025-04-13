package io.runnershigh.backend.shared.annotation

import io.runnershigh.backend.shared.config.QuerydslTestConfig
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@DataJpaTest
@Import(QuerydslTestConfig::class)
annotation class QuerydslTest
