package kometa.workstations.server.service

import kometa.workstations.server.model.User
import kometa.workstations.server.repository.RoleRepository
import kometa.workstations.server.repository.UserRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class UserService(
    private val repository: UserRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val passwordEncoder: PasswordEncoder,
    private val roleRepository: RoleRepository
) {
    private val PASSWORD_CACHE_PREFIX = "user:password:"

    @Cacheable("users")
    fun findById(id: Long): User? {
        return repository.findById(id).orElse(null)
    }

    fun findFiltered(username: String?, enabled: String?, role: String?, pageable: Pageable): Page<User> {
        return repository.findFiltered(
            username?.takeIf { it.isNotBlank() },
            enabled?.toBooleanStrictOrNull(),
            role?.takeIf { it.isNotBlank() },
            pageable
        )
    }

    @Cacheable("users")
    fun findAll(): List<User> {
        return repository.findAll()
    }

    @Cacheable("users")
    fun findByName(name: String): User? {
        return repository.findByUsername(name)
    }

    @Transactional
    @CacheEvict(value = ["users"], allEntries = true)
    fun save(user: User): User {
        val username = user.username
        val existingUser = repository.findByUsername(username)
        val passwordCacheKey = "$PASSWORD_CACHE_PREFIX$username"


        val newPassword = if (user.password.startsWith("{bcrypt}")) {
            user.password
        } else {
            passwordEncoder.encode(user.password)
        }

        if (existingUser == null) {
            user.password = newPassword
            redisTemplate.opsForValue().set(passwordCacheKey, newPassword, 10, TimeUnit.MINUTES)
        } else {
            if (user.password.isNotBlank() && !passwordEncoder.matches(user.password, existingUser.password)) {
                user.password = newPassword
                redisTemplate.opsForValue().set(passwordCacheKey, newPassword, 10, TimeUnit.MINUTES)
            } else {
                user.password = existingUser.password
                val cachedPassword = redisTemplate.opsForValue().get(passwordCacheKey)
                if (cachedPassword == null || cachedPassword != existingUser.password) {
                    redisTemplate.opsForValue().set(passwordCacheKey, existingUser.password, 10, TimeUnit.MINUTES)
                }
            }
        }

        return repository.save(user)
    }

    @Transactional
    @CacheEvict(value = ["users"], allEntries = true)
    fun registerUser(username: String, password: String): User {
        if (repository.findByUsername(username) != null) {
            throw IllegalArgumentException("Пользователь с таким именем уже существует")
        }

        val user = User(
            username = username,
            password = password,
            enabled = true,
            roles = setOf(roleRepository.findByName("ROLE_ADMIN")
                ?: throw IllegalStateException("Роль ROLE_USER не найдена"))
        )
        return save(user)
    }

    @CacheEvict(value = ["users"], allEntries = true)
    fun deleteById(id: Long) {
        val user = repository.findById(id).orElse(null)
        if (user != null) {
            val passwordCacheKey = "$PASSWORD_CACHE_PREFIX${user.username}"
            redisTemplate.delete(passwordCacheKey)
            repository.deleteById(id)
        }
    }
}