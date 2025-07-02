package io.runnershigh.backend.training.entity

import io.runnershigh.backend.shared.entity.BaseEntity
import io.runnershigh.backend.training.entity.enum.TrainingStatus
import io.runnershigh.backend.user.entity.UserEntity
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDate

@Entity
@Table(name = "training_schedules")
class TrainingSchedules(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: UserEntity,

    @Column(length = 100)
    @Comment("제목")
    var title: String,

    @Comment("장소")
    var location: String,

    @Comment("훈련예정 일자")
    @Temporal(TemporalType.DATE)
    var scheduledDate: LocalDate,

    @Comment("훈련 설명")
    @Lob
    var description: String,

    @Enumerated(EnumType.STRING)
    @Comment("훈련일정 상태")
    var status: TrainingStatus,

    @Comment("색상")
    var color: String,

    // 양방향 설정, 훈련일정 삭제시 그룹도 함께 삭제
    // orphanRemoval = 컬렉션에서 삭제시, DB에서도 삭제
    @OneToMany(mappedBy = "schedule", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val groups: MutableList<TrainingPlanGroups> = mutableListOf(),

//    @OneToMany(mappedBy = "schedule", cascade = [CascadeType.REMOVE], orphanRemoval = true)
//    val items: MutableList<TrainingPlanItems> = mutableListOf(),
) : BaseEntity()