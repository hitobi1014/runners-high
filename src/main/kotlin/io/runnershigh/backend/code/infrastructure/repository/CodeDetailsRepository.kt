package io.runnershigh.backend.code.infrastructure.repository

import io.runnershigh.backend.code.entity.CodeDetails
import org.springframework.data.jpa.repository.JpaRepository

interface CodeDetailsRepository : JpaRepository<CodeDetails, Int> {

    fun findByGroupId(codeGroupId: Int): List<CodeDetails>
}