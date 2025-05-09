package kometa.workstations.server.repository

import kometa.workstations.server.model.Software
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@Repository
@RepositoryRestResource
interface SoftwareRepository : JpaRepository<Software, Long> {
    fun findByNameContainingIgnoreCase(name: String): List<Software>
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Software>
}