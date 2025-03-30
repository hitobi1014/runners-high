package io.runnershigh.backend.code.entity

import io.runnershigh.backend.shared.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "code_groups")
class CodeGroups(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Embedded
    @AttributeOverride(name = "code", column = Column(nullable = false, unique = true))
    val codeValue: CommonCodeValue,
) : BaseEntity()