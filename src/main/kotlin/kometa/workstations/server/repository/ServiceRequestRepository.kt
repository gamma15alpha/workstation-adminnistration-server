package kometa.workstations.server.repository

import kometa.workstations.server.model.RequestStatus
import kometa.workstations.server.model.ServiceRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@Repository
@RepositoryRestResource
interface ServiceRequestRepository : JpaRepository<ServiceRequest, Long> {
    fun findByWorkstationId(workstationId: Long): List<ServiceRequest>
    fun findByRequesterUid(requesterUsername: String): List<ServiceRequest>
    fun findByAssignedEngineerUid(assignedEngineerUsername: String?): List<ServiceRequest>
    fun findByStatus(status: RequestStatus): List<ServiceRequest>
}