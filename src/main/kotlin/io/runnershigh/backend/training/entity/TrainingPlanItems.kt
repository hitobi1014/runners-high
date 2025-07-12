package io.runnershigh.backend.training.entity

import com.querydsl.core.annotations.PropertyType
import com.querydsl.core.annotations.QueryType
import io.runnershigh.backend.shared.entity.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.Duration

@Entity
@Table(name = "training_items")
class TrainingPlanItems(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = true)
    val group: TrainingPlanGroups? = null,

    @Comment("계획 순서")
    var itemOrder: Int,

    @Comment("훈련 목표")
    @Enumerated(EnumType.STRING)
    var targetType: TargetType,

    @Comment("목표 최소 페이스")
    val targetMinPace: Duration,

    @Comment("목표 최대 페이스")
    val targetMaxPace: Duration,

    @Comment("목표 평균 페이스")
    var targetAvgPace: Duration,

    @Comment("러닝타입코드")
    var runningTypeCode: Int,

    @Comment("거리 단위")
    @Enumerated(EnumType.STRING)
    var distanceUnit: DistanceUnit,

    @Comment("목표 거리")
    var targetDistance: Double,

    @Comment("목표 시간")
    var targetTime: Duration,

    // 훈련타입이 시간일때, 목표시간과 목표페이스를 기준으로 프론트에서 예상 거리 산출
    @Comment("예상 거리")
    var estimatedDistance: Double,

    // 훈련타입이 거리일때, 목표거리와 목표페이스를 기준으로 프론트에서 예상 시간 산출
    @Comment("예상 시간")
    var estimatedTime: Duration,

    @Comment("메모")
    @Lob
    var note: String?,
) : BaseEntity()