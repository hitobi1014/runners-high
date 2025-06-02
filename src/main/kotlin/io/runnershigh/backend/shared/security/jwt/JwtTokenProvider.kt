package io.runnershigh.backend.shared.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
//    private val userDetailService: UserDetailsService, // 사용여부 확인 후 제거
    @Value("\${jwt.secret-key}")
    private val secretString: String,

    @Value("\${jwt.access-token-expire-time}")
    private val accessTokenExpire: Long,

    @Value("\${jwt.refresh-token-expire-time}")
    private val refreshTokenExpire: Long,
) {

    private val logger = mu.KotlinLogging.logger {}

    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString))
    }


    /**
     * JWT 토큰을 생성합니다.
     *
     * 현재는 따로 회원 엔티티에 별도의 Role이 없으므로 하드코딩으로 삽입
     * @param userId 사용자의 고유 ID
     * @param nickname 사용자의 닉네임
     * @return 생성된 JWT 토큰 문자열
     */
    fun generateAccessToken(userId: Int, nickname: String): String {
        val claims = Jwts.claims().setSubject(userId.toString())
        claims["nickname"] = nickname
        claims["role"] = "ROLE_USER"

        val now = Date()
        val expiredTime = Date(now.time + accessTokenExpire * 1000)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiredTime)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun generateRefreshToken(userId: Int): String {
        val claims = Jwts.claims().setSubject(userId.toString())
        claims["tokenType"] = "REFRESH"

        val now = Date()
        val expiredTime = Date(now.time + refreshTokenExpire * 1000)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiredTime)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getUserId(token: String): Int {
        val claims = getClaims(token)
        return claims.subject.toInt()
    }

    fun getNickname(token: String): String {
        val claims = getClaims(token)
        return claims["nickname"].toString()
    }


    /**
     * JWT 토큰에서 인증 객체를 가져옵니다.
     *
     * @param token JWT 토큰
     * @return 인증(Authentication) 객체
     */
    fun getAuthentication(token: String): Authentication {
//        val userDetails = userDetailService.loadUserByUsername(getUserId(token).toString())

        val claims = getClaims(token)
        val userId = claims.subject.toInt()

        val role = claims["role"] as? String ?: "ROLE_USER"
        val authorities = listOf(SimpleGrantedAuthority(role))

//        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        return UsernamePasswordAuthenticationToken(userId, null, authorities)
    }


    /**
     * 주어진 JWT 토큰이 유효한지 검증합니다.
     *
     * @param token 유효성 검사를 수행할 JWT 토큰
     * @return 토큰이 유효하다면 true, 그렇지 않으면 false
     */
    fun validateToken(token: String): Boolean {
        try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)

            return !claims.body.expiration.before(Date())
        } catch (e: io.jsonwebtoken.ExpiredJwtException) {
            logger.debug("Token expired: ${e.message}")
            return false
        } catch (e: io.jsonwebtoken.UnsupportedJwtException) {
            logger.warn("Unsupported JWT token: ${e.message}")
            return false
        } catch (e: io.jsonwebtoken.MalformedJwtException) {
            logger.warn("Malformed JWT token: ${e.message}")
            return false
        } catch (e: Exception) {
            logger.error("Unexpected error validating JWT token", e)
            return false
        }
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
}