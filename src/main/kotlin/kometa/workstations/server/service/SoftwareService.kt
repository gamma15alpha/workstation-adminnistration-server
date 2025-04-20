package kometa.workstations.server.service

import kometa.workstations.server.model.Software
import kometa.workstations.server.repository.SoftwareRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SoftwareService(private val repository: SoftwareRepository) {
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