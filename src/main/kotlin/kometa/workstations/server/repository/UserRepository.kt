package kometa.workstations.server.repository

import kometa.workstations.server.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?

    @Query("""
    SELECT u FROM User u
    WHERE (:username IS NULL OR u.username LIKE %:username%)
    AND (:enabled IS NULL OR u.enabled = :enabled)
    AND (:role IS NULL OR EXISTS (
        SELECT 1 FROM u.roles r WHERE r.name LIKE %:role%
    ))
""")
    fun findFiltered(username: String?, enabled: Boolean?, role: String?, pageable: Pageable): Page<User>
}