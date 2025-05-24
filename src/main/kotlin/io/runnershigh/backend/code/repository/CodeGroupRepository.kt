package io.runnershigh.backend.code.repository

import io.runnershigh.backend.code.entity.CodeGroups
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CodeGroupRepository : JpaRepository<CodeGroups, Int> {

    @Query("SELECT c FROM CodeGroups c WHERE c.codeValue.code = :code")
    fun findByCode(@Param("code") code: String): CodeGroups?

    @Query("SELECT c FROM CodeGroups c WHERE c.codeValue.name = :name")
    fun findByName(@Param("name") name: String): CodeGroups?
}