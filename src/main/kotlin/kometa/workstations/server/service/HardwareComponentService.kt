package kometa.workstations.server.service

import kometa.workstations.server.model.ComponentStatus
import kometa.workstations.server.model.HardwareComponent
import kometa.workstations.server.repository.HardwareComponentRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HardwareComponentService(private val repository: HardwareComponentRepository) {
    @Cacheable("hardwareComponents")
    fun findById(id: Long): HardwareComponent? {
        return repository.findById(id).orElse(null)
    }

    @Cacheable("hardwareComponents")
    fun findAll(): List<HardwareComponent> {
        return repository.findAll()
    }

    @Cacheable("hardwareComponents")
    fun findByWorkstationId(workstationId: Long): List<HardwareComponent> {
        return repository.findByWorkstationId(workstationId)
    }

    @Cacheable("hardwareComponents")
    fun findByStatus(status: ComponentStatus): List<HardwareComponent> {
        return repository.findByStatus(status)
    }

    @Transactional
    @CacheEvict(value = ["hardwareComponents"], allEntries = true)
    fun save(component: HardwareComponent): HardwareComponent {
        return repository.save(component)
    }

    @Transactional
    @CacheEvict(value = ["hardwareComponents"], allEntries = true)
    fun deleteById(id: Long) {
        repository.deleteById(id)
    }
}