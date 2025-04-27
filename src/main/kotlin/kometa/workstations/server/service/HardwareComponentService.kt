package kometa.workstations.server.service

import kometa.workstations.server.model.ComponentStatus
import kometa.workstations.server.model.HardwareComponent
import kometa.workstations.server.repository.HardwareComponentRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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

    @Cacheable("hardwareComponents")
    fun findByModelOrSerialNumberPaginated(query: String, pageable: Pageable): Page<HardwareComponent> {
        return repository.findByModelContainingIgnoreCaseOrSerialNumberContainingIgnoreCase(query, query, pageable)
    }

    @Cacheable("hardwareComponents")
    fun findByTypeAndStatusPaginated(type: String, status: ComponentStatus, pageable: Pageable): Page<HardwareComponent> {
        return repository.findByTypeContainingIgnoreCaseAndStatus(type, status, pageable)
    }

    @Cacheable("hardwareComponents")
    fun findAllPaginated(pageable: Pageable): Page<HardwareComponent> {
        return repository.findAll(pageable)
    }

    @Transactional
    @CacheEvict(value = ["hardwareComponents"], allEntries = true)
    fun save(component: HardwareComponent): HardwareComponent {
        val isLinkedToWorkstation = component.workstation != null

        if (component.status == ComponentStatus.INACTIVE && isLinkedToWorkstation) {
            throw IllegalArgumentException("Невозможно установить статус 'Неактивный' для компонента, привязанного к рабочей станции")
        }

        if (component.status == ComponentStatus.ACTIVE && !isLinkedToWorkstation) {
            throw IllegalArgumentException("Невозможно установить статус 'Активный' для компонента без привязки к рабочей станции")
        }

        return repository.save(component)
    }

    @Transactional
    @CacheEvict(value = ["hardwareComponents"], allEntries = true)
    fun deleteById(id: Long) {
        repository.deleteById(id)
    }
}