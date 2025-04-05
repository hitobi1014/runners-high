package io.runnershigh.backend.training.infrastructure.entity

import io.runnershigh.backend.shared.entity.BaseEntity
import io.runnershigh.backend.training.domain.enum.TargetType
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalTime

@Entity
@Table(name = "training_items")
class TrainingPlanItems(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    val schedule: TrainingSchedules,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    val group: TrainingPlanGroups,

    @Comment("계획 순서")
    var itemOrder: Int,

    @Comment("훈련 목표")
    var targetType: TargetType,

    @Temporal(TemporalType.TIME)
    @Comment("목표 페이스")
    var targetPace: LocalTime,

    @Temporal(TemporalType.TIME)
    @Comment("목표 페이스 시작 구간")
    val targetStartPace: LocalTime,

    @Temporal(TemporalType.TIME)
    @Comment("목표 페이스 종료 구간")
    val targetEndPace: LocalTime,

    @Comment("러닝타입코드")
    var runningTypeCode: Long,

    @Comment("목표 거리")
    var targetDistance: Double,

    @Comment("목표 시간")
    var targetTime: LocalTime,

    @Comment("예상 거리")
    var estimatedDistance: Double,

    @Comment("예상 시간")
    var estimatedTime: LocalTime,

    @Comment("메모")
    @Lob
    var note: String,
) : BaseEntity()