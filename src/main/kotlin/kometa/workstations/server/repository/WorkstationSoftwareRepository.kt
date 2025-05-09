package kometa.workstations.server.repository

import kometa.workstations.server.model.Software
import kometa.workstations.server.model.Workstation
import kometa.workstations.server.model.WorkstationSoftware
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@Repository
@RepositoryRestResource
interface WorkstationSoftwareRepository : JpaRepository<WorkstationSoftware, Long> {
    fun findByWorkstationId(workstationId: Long): List<WorkstationSoftware>
    fun findBySoftwareId(softwareId: Long): List<WorkstationSoftware>
    fun countBySoftware(software: Software): Long
    fun existsByWorkstationAndSoftware(workstation: Workstation, software: Software): Boolean
}