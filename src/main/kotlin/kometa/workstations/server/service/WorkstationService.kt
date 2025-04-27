package kometa.workstations.server.service

import jakarta.transaction.Transactional
import kometa.workstations.server.model.Workstation
import kometa.workstations.server.repository.WorkstationRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class WorkstationService(private val repository: WorkstationRepository) {

    @Cacheable("workstations")
    fun findById(id: Long): Workstation? {
        return repository.findById(id).orElse(null)
    }

    @Cacheable("workstations")
    fun findAll(): List<Workstation> {
        return repository.findAll()
    }

    @Cacheable("workstations")
    fun findByName(name: String): List<Workstation> {
        return repository.findByNameContainingIgnoreCase(name)
    }

    @Cacheable("workstations")
    fun searchByNameOrInventory(query: String): List<Workstation> {
        return repository.findByNameContainingIgnoreCaseOrInventoryNumberContainingIgnoreCase(query, query)
    }


    @Cacheable("workstations")
    fun findByAssignedUser(assignedUser: String): List<Workstation> {
        return repository.findByAssignedUserUidContainingIgnoreCase(assignedUser)
    }

    @Transactional
    @CacheEvict(value = ["workstations"], allEntries = true)
    fun save(workstation: Workstation): Workstation {
        return repository.save(workstation)
    }

    @Transactional
    @CacheEvict(value = ["workstations"], allEntries = true)
    fun deleteById(id: Long) {
        repository.deleteById(id)
    }
}