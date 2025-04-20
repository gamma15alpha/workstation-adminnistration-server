package kometa.workstations.server.repository

import kometa.workstations.server.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@Repository
@RepositoryRestResource
interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(name: String): Role?
}