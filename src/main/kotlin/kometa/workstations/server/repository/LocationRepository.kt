package kometa.workstations.server.repository

import kometa.workstations.server.model.Location
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@Repository
@RepositoryRestResource
interface LocationRepository : JpaRepository<Location, Long> {
    fun findByNameContainingIgnoreCase(name: String): List<Location>
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Location>
}