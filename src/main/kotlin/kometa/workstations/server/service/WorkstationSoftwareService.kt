package kometa.workstations.server.service

import kometa.workstations.server.model.WorkstationSoftware
import kometa.workstations.server.repository.WorkstationSoftwareRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WorkstationSoftwareService(private val repository: WorkstationSoftwareRepository) {
    @Cacheable("workstationSoftware")
    fun findById(id: Long): WorkstationSoftware? {
        return repository.findById(id).orElse(null)
    }

    @Cacheable("workstationSoftware")
    fun findAll(): List<WorkstationSoftware> {
        return repository.findAll()
    }

    @Cacheable("workstationSoftware")
    fun findByWorkstationId(workstationId: Long): List<WorkstationSoftware> {
        return repository.findByWorkstationId(workstationId)
    }

    @Cacheable("workstationSoftware")
    fun findBySoftwareId(softwareId: Long): List<WorkstationSoftware> {
        return repository.findBySoftwareId(softwareId)
    }

    @Transactional
    @CacheEvict(value = ["workstationSoftware"], allEntries = true)
    fun save(workstationSoftware: WorkstationSoftware): WorkstationSoftware {
        return repository.save(workstationSoftware)
    }

    @Transactional
    @CacheEvict(value = ["workstationSoftware"], allEntries = true)
    fun deleteById(id: Long) {
        repository.deleteById(id)
    }
}