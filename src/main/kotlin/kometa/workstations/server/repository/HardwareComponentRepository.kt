package kometa.workstations.server.repository

import kometa.workstations.server.model.ComponentStatus
import kometa.workstations.server.model.HardwareComponent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@Repository
@RepositoryRestResource
interface HardwareComponentRepository : JpaRepository<HardwareComponent, Long> {
    fun findByWorkstationId(workstationId: Long): List<HardwareComponent>
    fun findByStatus(status: ComponentStatus): List<HardwareComponent>
}