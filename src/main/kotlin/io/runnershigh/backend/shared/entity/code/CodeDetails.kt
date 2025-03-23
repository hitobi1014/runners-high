package io.runnershigh.backend.shared.entity.code

import io.runnershigh.backend.shared.entity.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(
    name = "code_details",
    // 비즈니스 규칙, 그룹ID와 상세코드는 값이 같으면 안됨
    indexes = [
        Index(
            name = "idx_code_details_group_code",
            columnList = "group_id,code",
            unique = true
        )
    ]
)
class CodeDetails(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @Comment("코드 그룹ID")
    val group: CodeGroups,

    @Embedded
    val codeValue: CommonCodeValue,
) : BaseEntity()