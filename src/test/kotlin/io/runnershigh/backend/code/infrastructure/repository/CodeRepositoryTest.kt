package io.runnershigh.backend.code.infrastructure.repository

import io.runnershigh.backend.code.entity.CodeGroups
import io.runnershigh.backend.code.entity.CommonCodeValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DataJpaTest
@Sql("/test-data.sql")
class CodeRepositoryTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var codeGroupRepository: CodeGroupRepository

    @Autowired
    private lateinit var codeDetailsRepository: CodeDetailsRepository

    @Test
    @DisplayName("코드 그룹 저장")
    fun createCodeGroup() {
        //given
        val codeGroup = CodeGroups(
            codeValue = CommonCodeValue(
                code = "testCode",
                name = "테스트코드",
                description = "설명",
            )
        )

        //when
        val savedCodeGroup = codeGroupRepository.save(codeGroup)
        entityManager.flush()
        entityManager.clear()

        //then
        val foundCodeGroup = codeGroupRepository.findById(savedCodeGroup.id).orElse(null)
        assertNotNull(foundCodeGroup)
        assertEquals("테스트코드", foundCodeGroup.codeValue.name)
        assertEquals("testCode", foundCodeGroup.codeValue.code)
    }

    @Test
    @DisplayName("코드 조회 - 코드")
    fun getCodeGroupByCode() {
        //when
        val foundCodeGroup = codeGroupRepository.findByCode("SYSTEM")

        //then
        assertNotNull(foundCodeGroup, "Code group with code 'SYSTEM' should exist")
        assertTrue(foundCodeGroup.codeValue.isActive)
        assertEquals("SYSTEM", foundCodeGroup.codeValue.code)
        assertEquals("시스템코드", foundCodeGroup.codeValue.name)
    }

    @Test
    @DisplayName("코드 조회 - 코드명")
    fun getCodeGroupByName() {
        //when
        val foundCodeGroup = codeGroupRepository.findByName("시스템코드")

        //then
        assertNotNull(foundCodeGroup, "Code group with name '시스템코드' should exist")
        assertTrue(foundCodeGroup.codeValue.isActive)
        assertEquals("SYSTEM", foundCodeGroup.codeValue.code)
        assertEquals("시스템코드", foundCodeGroup.codeValue.name)
    }

    @Test
    @DisplayName("코드그룹으로 상세코드 조회")
    fun getCodeDetailsByCodeGroup() {
        //when
        val foundDetails = codeDetailsRepository.findByGroupId(99)

        //then
        assertEquals(3, foundDetails.size)
        assertEquals("CODE1", foundDetails[0].codeValue.code)
    }

}