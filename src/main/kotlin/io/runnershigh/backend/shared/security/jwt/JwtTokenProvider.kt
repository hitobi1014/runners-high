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
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
//    private val userDetailService: UserDetailsService, // 사용여부 확인 후 제거
    @Value("\${jwt.secret-key}")
    private val secretString: String,

    @Value("\${jwt.access-token-expire-time}")
    private val tokenExpireSeconds: Long,
) {

    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString))
    }

    fun generateToken(userId: Int, nickname: String): String {
        val claims = Jwts.claims().setSubject(userId.toString())
        claims["nickname"] = nickname
        claims["role"] = "ROLE_USER"

        val now = Date()
        val expiredTime = Date(now.time + tokenExpireSeconds * 1000)

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

    fun getAuthentication(token: String): Authentication {
//        val userDetails = userDetailService.loadUserByUsername(getUserId(token).toString())

        val claims = getClaims(token)
        val userId = claims.subject.toInt()

        val role = claims["role"] as? String ?: "ROLE_USER"
        val authorities = listOf(SimpleGrantedAuthority(role))

//        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        return UsernamePasswordAuthenticationToken(userId, null, authorities)
    }

    fun validateToken(token: String): Boolean {
        try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)

            return !claims.body.expiration.before(Date())
        } catch (e: Exception) {
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