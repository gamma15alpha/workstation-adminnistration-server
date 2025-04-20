package kometa.workstations.server.service

import kometa.workstations.server.model.RequestStatus
import kometa.workstations.server.model.ServiceRequest
import kometa.workstations.server.repository.ServiceRequestRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ServiceRequestService(private val repository: ServiceRequestRepository) {
    @Cacheable("serviceRequests")
    fun findById(id: Long): ServiceRequest? {
        return repository.findById(id).orElse(null)
    }

    @Cacheable("serviceRequests")
    fun findAll(): List<ServiceRequest> {
        return repository.findAll()
    }

    @Cacheable("serviceRequests")
    fun findByWorkstationId(workstationId: Long): List<ServiceRequest> {
        return repository.findByWorkstationId(workstationId)
    }

    @Cacheable("serviceRequests")
    fun findByRequesterUsername(requesterUsername: String): List<ServiceRequest> {
        return repository.findByRequesterUid(requesterUsername)
    }

    @Cacheable("serviceRequests")
    fun findByAssignedEngineerUsername(assignedEngineerUsername: String?): List<ServiceRequest> {
        return repository.findByAssignedEngineerUid(assignedEngineerUsername)
    }

    @Cacheable("serviceRequests")
    fun findByStatus(status: RequestStatus): List<ServiceRequest> {
        return repository.findByStatus(status)
    }

    @Transactional
    @CacheEvict(value = ["serviceRequests"], allEntries = true)
    fun save(serviceRequest: ServiceRequest): ServiceRequest {
        return repository.save(serviceRequest)
    }

    @Transactional
    @CacheEvict(value = ["serviceRequests"], allEntries = true)
    fun deleteById(id: Long) {
        repository.deleteById(id)
    }
}