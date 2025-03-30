package io.runnershigh.backend.user.infrastructure.entity

import io.runnershigh.backend.shared.entity.BaseEntity
import io.runnershigh.backend.user.domain.enum.AgeGroup
import io.runnershigh.backend.user.domain.enum.Gender
import io.runnershigh.backend.user.domain.enum.UserStatus
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(unique = true, length = 30, nullable = false)
    @Comment("로그인 ID")
    val loginId: String,

    @Column(nullable = false)
    @Comment("비밀번호")
    var password: String,

    @Column(nullable = false)
    @Comment("닉네임")
    var nickname: String,

    @Enumerated(EnumType.STRING)
    @Comment("성별")
    var gender: Gender,

    @Comment("프로필")
    var profileImage: String,

    @Comment("연령대")
    var ageGroup: AgeGroup,

    @Embedded
    val runningStats: RunningStats = RunningStats(),

    @Enumerated(EnumType.STRING)
    @Comment("회원 상태")
    @Column(nullable = false)
    var userStatus: UserStatus = UserStatus.ACTIVE,
) : BaseEntity()