package kometa.workstations.server.repository

import kometa.workstations.server.model.ComponentStatus
import kometa.workstations.server.model.HardwareComponent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@Repository
@RepositoryRestResource
interface HardwareComponentRepository : JpaRepository<HardwareComponent, Long> {
    fun findByWorkstationId(workstationId: Long): List<HardwareComponent>
    fun findByStatus(status: ComponentStatus): List<HardwareComponent>
    fun findByModelContainingIgnoreCaseOrSerialNumberContainingIgnoreCase(
        model: String,
        serialNumber: String,
        pageable: Pageable
    ): Page<HardwareComponent>
    fun findByTypeContainingIgnoreCaseAndStatus(
        type: String,
        status: ComponentStatus,
        pageable: Pageable
    ): Page<HardwareComponent>

}