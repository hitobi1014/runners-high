package io.runnershigh.backend.training.infrastructure.entity

import io.runnershigh.backend.shared.entity.BaseEntity
import io.runnershigh.backend.training.domain.enum.DistanceUnit
import io.runnershigh.backend.training.domain.enum.TargetType
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalTime

@Entity
@Table(name = "training_items")
class TrainingPlanItems(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    val schedule: TrainingSchedules,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = true)
    val group: TrainingPlanGroups? = null,

    @Comment("계획 순서")
    var itemOrder: Int,

    @Comment("훈련 목표")
    var targetType: TargetType,

    @Temporal(TemporalType.TIME)
    @Comment("목표 최소 페이스")
    val targetMinPace: LocalTime,

    @Temporal(TemporalType.TIME)
    @Comment("목표 최대 페이스")
    val targetMaxPace: LocalTime,

    @Temporal(TemporalType.TIME)
    @Comment("목표 평균 페이스")
    var targetAvgPace: LocalTime,

    @Comment("러닝타입코드")
    var runningTypeCode: Int,

    @Comment("거리 단위")
    @Enumerated(EnumType.STRING)
    var distanceUnit: DistanceUnit?,

    @Comment("목표 거리")
    var targetDistance: Double?,

    @Comment("목표 시간")
    var targetTime: LocalTime?,

    @Comment("예상 거리")
    var estimatedDistance: Double?,

    @Comment("예상 시간")
    var estimatedTime: LocalTime?,

    @Comment("메모")
    @Lob
    var note: String?,
) : BaseEntity()