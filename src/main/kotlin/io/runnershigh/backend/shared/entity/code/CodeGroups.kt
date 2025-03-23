package io.runnershigh.backend.shared.entity.code

import io.runnershigh.backend.shared.entity.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(name = "code_groups")
class CodeGroups(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Embedded
    @AttributeOverride(name = "code", column = Column(nullable = false, unique = true))
    val codeValue: CommonCodeValue,
) : BaseEntity()