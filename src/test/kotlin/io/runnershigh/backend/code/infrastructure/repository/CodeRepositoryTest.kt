package io.runnershigh.backend.code.infrastructure.repository

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.runnershigh.backend.code.entity.CodeGroups
import io.runnershigh.backend.code.entity.CommonCodeValue
import io.runnershigh.backend.code.repository.CodeDetailsRepository
import io.runnershigh.backend.code.repository.CodeGroupRepository
import io.runnershigh.backend.shared.annotation.QuerydslTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

@QuerydslTest
@Sql("/test-data.sql")
class CodeRepositoryTest : BehaviorSpec() {
    @Autowired
    private lateinit var codeGroupRepository: CodeGroupRepository

    @Autowired
    private lateinit var codeDetailsRepository: CodeDetailsRepository

    init {
        Given("코드 그룹 정보를 작성하고") {
            val code = "testCode"
            val codeGroup = CodeGroups(
                codeValue = CommonCodeValue(
                    code = code,
                    name = "테스트코드",
                    description = "설명",
                )
            )

            When("저장을 수행하면") {
                codeGroupRepository.saveAndFlush<CodeGroups>(codeGroup)

                val foundCodeGroup = codeGroupRepository.findByCode(code)

                Then("저장을 성공한다.") {
                    foundCodeGroup shouldNotBe null
                    foundCodeGroup?.codeValue?.name shouldBe "테스트코드"
                    foundCodeGroup?.codeValue?.code shouldBe code
                }
            }
        }
    }
}