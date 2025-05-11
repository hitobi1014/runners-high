package io.runnershigh.backend.init

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
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

    @Value("\${app.init.skip-data}")
    private val skipDataInit: Boolean,
) : ApplicationListener<ContextClosedEvent> {

    private val logger = KotlinLogging.logger {}

    override fun onApplicationEvent(event: ContextClosedEvent) {
        if (skipDataInit) {
            logger.info { "Skip data 활성화 > 데이터 삭제 pass" }
            return
        }

        logger.info { " 서버 종료 중 .. DB 테이블 데이터 삭제" }
        truncateAllTables()
    }

    private fun truncateAllTables() {
        try {
            dataSource.connection.use { connection ->
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

                resetIdentityColumns(connection)
            }
        } catch (e: SQLException) {
            logger.error(e) { "DB 비우기 실패: ${e.message}" }
        }
    }

    // 시퀀스 초기화 (ID가 1부터 다시 시작하게)
    private fun resetIdentityColumns(connection: Connection) {
        try {
            // 테이블 목록 가져오기
            val tableQuery = """
                SELECT table_name 
                FROM information_schema.tables 
                WHERE table_schema = 'public' 
                AND table_type = 'BASE TABLE'
            """.trimIndent()

            val tableList = mutableListOf<String>()
            connection.prepareStatement(tableQuery).use { stmt ->
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        tableList.add(rs.getString("table_name"))
                    }
                }
            }

            // 각 테이블의 IDENTITY 컬럼 초기화
            connection.createStatement().use { stmt ->
                tableList.forEach { tableName ->
                    try {
                        stmt.executeUpdate("ALTER TABLE $tableName ALTER COLUMN id RESTART WITH 1")
                        logger.debug { "IDENTITY 컬럼 리셋: $tableName" }
                    } catch (e: SQLException) {
                        // 테이블에 id 컬럼이 없거나 IDENTITY가 아닌 경우 무시
                        logger.debug { "IDENTITY 리셋 실패 (무시해도 됨): $tableName - ${e.message}" }
                    }
                }
            }

        } catch (e: SQLException) {
            logger.warn { "IDENTITY 컬럼 리셋 실패: ${e.message}" }
        }
    }
}