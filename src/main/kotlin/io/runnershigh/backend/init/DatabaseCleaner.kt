package io.runnershigh.backend.init

import mu.KotlinLogging
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextClosedEvent
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

@Component
@Profile("local")
class DatabaseCleaner(
    private val dataSource: DataSource,
) : ApplicationListener<ContextClosedEvent> {

    private val logger = KotlinLogging.logger {}

    override fun onApplicationEvent(event: ContextClosedEvent) {
        logger.info { " 서버 종료 중 .. DB 테이블 데이터 삭제" }
        truncateAllTables()
    }

    private fun truncateAllTables() {
        var connection: Connection? = null
        try {

            connection = dataSource.connection
            val schemaName = "public"

            val tables = mutableListOf<String>()
            val tableQuery = """
               SELECT table_name 
               FROM information_schema.tables 
               WHERE table_schema = ? 
               AND table_type = 'BASE TABLE'
           """.trimIndent()

            connection.prepareStatement(tableQuery).use { stmt ->
                stmt.setString(1, schemaName)

                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        val tableName = rs.getString("table_name")
                        // 시스템 테이블 제외
                        if (!tableName.startsWith("pg_") && !tableName.startsWith("flyway_")) {
                            tables.add(tableName)
                        }
                    }
                }
            }

            logger.info { "총 ${tables.size}개 테이블 : ${tables.joinToString()}" }

            connection.createStatement().use { stmt ->
                // 테이블을 역순으로 처리하면 외래키 문제가 덜 발생함
                tables.reversed().forEach { tableName ->
                    try {
                        logger.debug { "테이블 데이터 삭제 중: $tableName" }
                        // TRUNCATE 대신 DELETE 사용
                        stmt.executeUpdate("DELETE FROM $tableName")
                    } catch (e: SQLException) {
                        logger.warn { "테이블 비우기 실패: $tableName - ${e.message}" }
                    }
                }
            }

            logger.info { "모든 테이블 비우기 작업 완료" }
            resetSequences(connection, tables)

        } catch (e: SQLException) {
            logger.error(e) { "DB 비우기 실패: ${e.message}" }
        } finally {
            try {
                connection?.close()
            } catch (e: SQLException) {
                logger.error(e) { "DB 연결 닫기 실패" }
            }
        }
    }

    // 시퀀스 초기화 (ID가 1부터 다시 시작하게)
    private fun resetSequences(connection: Connection, tables: List<String>) {
        try {
            val sequenceQuery = """
                SELECT sequence_name 
                FROM information_schema.sequences 
                WHERE sequence_schema = 'public'
            """.trimIndent()

            val sequences = mutableListOf<String>()
            connection.prepareStatement(sequenceQuery).use { stmt ->
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        sequences.add(rs.getString("sequence_name"))
                    }
                }
            }

            connection.createStatement().use { stmt ->
                sequences.forEach { sequenceName ->
                    try {
                        // 시퀀스 1로 리셋
                        stmt.executeUpdate("ALTER SEQUENCE $sequenceName RESTART WITH 1")
                        logger.debug { "시퀀스 리셋: $sequenceName" }
                    } catch (e: SQLException) {
                        logger.warn { "시퀀스 리셋 실패: $sequenceName - ${e.message}" }
                    }
                }
            }
        } catch (e: SQLException) {
            logger.warn { "시퀀스 리셋 실패: ${e.message}" }
        }
    }
}