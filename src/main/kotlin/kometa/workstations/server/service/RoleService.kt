package kometa.workstations.server.service

import kometa.workstations.server.model.Role
import kometa.workstations.server.repository.RoleRepository
import org.springframework.cache.annotation.CacheEvict import org.springframework.cache.annotation.Cacheable import org.springframework.stereotype.Service import org.springframework.transaction.annotation.Transactional

@Service class RoleService(private val repository: RoleRepository) {
    @Cacheable("roles")
    fun findById(id: Long): Role? {
        return repository.findById(id).orElse(null)
    }

    @Cacheable("roles")
    fun findAll(): List<Role> {
        return repository.findAll()
    }

    @Cacheable("roles")
    fun findByName(name: String): Role? {
        return repository.findByName(name)
    }

    @Transactional
    @CacheEvict(value = ["roles"], allEntries = true)
    fun save(role: Role): Role {
        return repository.save(role)
    }

    @Transactional
    @CacheEvict(value = ["roles"], allEntries = true)
    fun deleteById(id: Long) {
        repository.deleteById(id)
    }
}