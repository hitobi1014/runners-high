package io.runnershigh.backend.shared.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.Comment
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    @Comment("생성일시")
    var createdAt: LocalDateTime? = null
        protected set

    @LastModifiedDate
    @Column(updatable = true)
    @Comment("수정일시")
    var updatedAt: LocalDateTime? = null
        protected set
}