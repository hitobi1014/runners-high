package io.runnershigh.backend.training.entity

import io.runnershigh.backend.shared.entity.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(name = "training_groups")
class TrainingPlanGroups(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    val schedule: TrainingSchedules,

    @Comment("그룹내 순서")
    var groupOrder: Int,

    @Comment("반복횟수")
    var repeatCount: Int,

    @Lob
    @Comment("그룹 설명")
    var description: String,

    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<TrainingPlanItems> = mutableListOf(),

    ) : BaseEntity()