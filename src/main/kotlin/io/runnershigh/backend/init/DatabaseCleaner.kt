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
                    // 의존성 순서에 따라 테이블 삭제 (자식 테이블부터 부모 테이블 순으로)
                    val orderedTables = getTablesInDeletionOrder(tables)
                    
                    orderedTables.forEach { tableName ->
                        try {
                            logger.debug { "테이블 데이터 삭제 중: $tableName" }
                            // CASCADE 옵션으로 외래키 제약조건 무시하고 삭제
                            stmt.executeUpdate("TRUNCATE TABLE $tableName RESTART IDENTITY CASCADE")
                        } catch (e: SQLException) {
                            logger.warn { "TRUNCATE 실패, DELETE 시도: $tableName - ${e.message}" }
                            try {
                                // TRUNCATE가 실패하면 DELETE 사용
                                stmt.executeUpdate("DELETE FROM $tableName")
                            } catch (e2: SQLException) {
                                logger.warn { "테이블 비우기 완전 실패: $tableName - ${e2.message}" }
                            }
                        }
                    }
                }
                logger.info { "모든 테이블 비우기 작업 완료" }
            }
        } catch (e: SQLException) {
            logger.error(e) { "DB 비우기 실패: ${e.message}" }
        }
    }

    // 외래키 의존성에 따라 테이블 삭제 순서 결정
    private fun getTablesInDeletionOrder(tables: List<String>): List<String> {
        // 자식 테이블부터 부모 테이블 순으로 정렬
        val orderedTables = mutableListOf<String>()
        
        // 1. 가장 하위 자식 테이블들 (다른 테이블을 참조하지 않는 테이블들)
        tables.filter { it.startsWith("training_items") || it.startsWith("training_plan_items") }.forEach { orderedTables.add(it) }
        tables.filter { it.startsWith("training_groups") || it.startsWith("training_plan_groups") }.forEach { orderedTables.add(it) }
        
        // 2. 중간 테이블들 (users를 참조하는 테이블들)
        tables.filter { it.startsWith("training_schedules") }.forEach { orderedTables.add(it) }
        tables.filter { it.startsWith("training_") && !orderedTables.contains(it) }.forEach { orderedTables.add(it) }
        
        // 3. 부모 테이블 (users)
        tables.filter { it == "users" }.forEach { orderedTables.add(it) }
        
        // 4. 나머지 테이블들
        tables.filter { !orderedTables.contains(it) }.forEach { orderedTables.add(it) }
        
        logger.debug { "테이블 삭제 순서: ${orderedTables.joinToString()}" }
        return orderedTables
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