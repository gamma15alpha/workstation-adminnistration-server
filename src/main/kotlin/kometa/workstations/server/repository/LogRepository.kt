package kometa.workstations.server.repository

import kometa.workstations.server.model.Log
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@Repository
@RepositoryRestResource
interface LogRepository : JpaRepository<Log, Long> {
    fun findByUsername(userLdapUid: String): List<Log>
    fun findByEntityTypeAndEntityId(entityType: String?, entityId: Long?): List<Log>
    fun findByActionContainingIgnoreCase(action: String, pageable: Pageable): Page<Log>
}