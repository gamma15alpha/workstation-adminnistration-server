package kometa.workstations.server.service

import kometa.workstations.server.model.Software
import kometa.workstations.server.repository.SoftwareRepository
import kometa.workstations.server.repository.WorkstationSoftwareRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SoftwareService(
    private val repository: SoftwareRepository,
    private val workstationSoftwareService: WorkstationSoftwareService,
) {
    @Cacheable("software")
    fun findById(id: Long): Software? {
        return repository.findById(id).orElse(null)
    }

    @Cacheable("software")
    fun findAll(): List<Software> {
        return repository.findAll()
    }

    @Cacheable("software")
    fun findByName(name: String): List<Software> {
        return repository.findByNameContainingIgnoreCase(name)
    }

    @Cacheable("software")
    fun findByNamePaginated(name: String, pageable: Pageable): Page<Software> {
        return repository.findByNameContainingIgnoreCase(name, pageable)
    }

    fun canInstall(software: Software): Boolean {
        if (software.licenseCount == null) return true
        val used = workstationSoftwareService.countBySoftware(software)
        return used < software.licenseCount
    }

    @Cacheable("software")
    fun countUsedLicenses(software: Software): Long {
        return workstationSoftwareService.countBySoftware(software)
    }


    @Transactional
    @CacheEvict(value = ["software"], allEntries = true)
    fun save(software: Software): Software {
        return repository.save(software)
    }

    @Transactional
    @CacheEvict(value = ["software"], allEntries = true)
    fun deleteById(id: Long) {
        repository.deleteById(id)
    }
}