package io.runnershigh.backend.user.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.hibernate.annotations.Comment

@Embeddable
class RunningStats(
    @Comment("누적 주행거리(km)")
    var totalDistance: Double = 0.0,

    @Comment("누적 주행시간(초)")
    var totalTime: Int = 0,

    @Comment("총 러닝 횟수")
    var totalRuns: Int = 0,

    @Comment("레벨")
    var level: Int = 0,
)