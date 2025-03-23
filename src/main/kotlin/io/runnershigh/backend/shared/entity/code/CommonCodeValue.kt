package io.runnershigh.backend.shared.entity.code

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.hibernate.annotations.Comment

@Embeddable
class CommonCodeValue(
    @Comment("코드")
    @Column(name = "code", nullable = false)
    var code: String,

    @Comment("코드명")
    @Column(name = "name", nullable = false)
    var name: String,

    @Comment("설명")
    @Column(name = "description")
    var description: String,

    @Comment("활성화여부")
    @Column(name = "isActive", nullable = false)
    var isActive: Boolean = true,
)