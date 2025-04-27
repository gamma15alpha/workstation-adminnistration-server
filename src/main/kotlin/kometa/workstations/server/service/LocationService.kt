package kometa.workstations.server.service

import kometa.workstations.server.model.Location
import kometa.workstations.server.repository.LocationRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LocationService(private val repository: LocationRepository) {
    @Cacheable("locations")
    fun findById(id: Long): Location? {
        return repository.findById(id).orElse(null)
    }

    @Cacheable("locations")
    fun findAll(): List<Location> {
        return repository.findAll()
    }

    @Cacheable("locations")
    fun findByName(name: String): List<Location> {
        return repository.findByNameContainingIgnoreCase(name)
    }

    @Cacheable("locations")
    fun findByNamePaginated(name: String, pageable: Pageable): Page<Location> {
        return repository.findByNameContainingIgnoreCase(name, pageable)
    }


    @Transactional
    @CacheEvict(value = ["locations"], allEntries = true)
    fun save(location: Location): Location {
        return repository.save(location)
    }

    @Transactional
    @CacheEvict(value = ["locations"], allEntries = true)
    fun deleteById(id: Long) {
        repository.deleteById(id)
    }
}