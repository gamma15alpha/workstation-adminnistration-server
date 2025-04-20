package kometa.workstations.server.repository

import kometa.workstations.server.model.Workstation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@Repository
@RepositoryRestResource
interface WorkstationRepository : JpaRepository<Workstation, Long> {
    fun findByNameContainingIgnoreCase(name: String): List<Workstation>
    fun findByAssignedUserUid(assignedUser: String): List<Workstation>
}