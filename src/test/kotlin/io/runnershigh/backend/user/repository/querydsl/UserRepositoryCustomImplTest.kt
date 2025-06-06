package io.runnershigh.backend.user.repository.querydsl

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.runnershigh.backend.fixture.UserFixture
import io.runnershigh.backend.shared.annotation.QuerydslTest
import io.runnershigh.backend.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@QuerydslTest
class UserRepositoryCustomImplTest : BehaviorSpec() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    init {
        beforeEach {
            val user = UserFixture.createDefault(loginId = "test1", nickname = "테스트1")
            testEntityManager.persistAndFlush(user)
            testEntityManager.clear()
        }

        given("이미 등록된 로그인ID가 있을 때") {
            `when`("로그인ID 중복검사를 진행하면") {
                val loginId = "test1"
                then("중복된 로그인 ID가 존재하면 true를 반환한다.") {
                    userRepository.existsByLoginId(loginId) shouldBe true
                }
            }
        }

        given("이미 등록된 닉네임이 있을 때") {
            `when`("닉네임 중복검사를 진행하면") {
                val user = userRepository.findByLoginId("test1")
                println("유저ID: ${user?.loginId}, 유저닉네임: ${user?.nickname}")
                val nickname = "테스트1"
                then("중복된 닉네임이 존재하면 true를 반환한다.") {
                    userRepository.existsByNickname(nickname) shouldBe true
                }
            }
        }
    }
}
