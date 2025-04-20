package kometa.workstations.server.service

import kometa.workstations.server.repository.UserRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User as SpringUSer
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import kometa.workstations.server.model.User
import java.util.concurrent.TimeUnit

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
    private val redisTemplate: RedisTemplate<String, String>
) : UserDetailsService {
    private val PASSWORD_CACHE_PREFIX = "user:password:"

    override fun loadUserByUsername(username: String): UserDetails {
        val passwordCacheKey = "$PASSWORD_CACHE_PREFIX$username"
        val cachedPassword = redisTemplate.opsForValue().get(passwordCacheKey)

        val user = if (cachedPassword != null) {

            userRepository.findByUsername(username)
                ?: throw UsernameNotFoundException("User not found with username: $username")
        } else {

            val dbUser = userRepository.findByUsername(username)
                ?: throw UsernameNotFoundException("User not found with username: $username")

            redisTemplate.opsForValue().set(passwordCacheKey, dbUser.password, 10, TimeUnit.MINUTES)

            dbUser
        }
        if(user.password != cachedPassword)
        throw UsernameNotFoundException("Cached password mismatch for username: $username")

        return SpringUSer(
            user.username,
            user.password,
            user.enabled,
            true,
            true,
            true,
            user.roles.map { SimpleGrantedAuthority(it.name) }
        )
    }
}