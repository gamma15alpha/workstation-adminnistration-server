package kometa.workstations.server.service

import kometa.workstations.server.model.Log
import kometa.workstations.server.repository.LogRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service class LogService(private val repository: LogRepository) {
    fun findById(id: Long): Log? {
        return repository.findById(id).orElse(null)
    }

    fun findLogsPaginated(search: String, pageable: Pageable): Page<Log> {
        return if (search.isBlank()) {
            repository.findAll(pageable)
        } else {
            repository.findByActionContainingIgnoreCase(search, pageable)
        }
    }

    fun findAll(): List<Log> {
        return repository.findAll()
    }

    fun findByUserUsername(userUsername: String): List<Log> {
        return repository.findByUsername(userUsername)
    }

    fun findByEntityTypeAndEntityId(entityType: String?, entityId: Long?): List<Log> {
        return repository.findByEntityTypeAndEntityId(entityType, entityId)
    }

    @Transactional
    fun save(log: Log): Log {
        return repository.save(log)
    }

    @Transactional
    fun deleteById(id: Long) {
        repository.deleteById(id)
    }

    fun logAction(username: String, action: String, entityType: String?, entityId: Long?) {
        val log = Log(
            username = username,
            action = action,
            entityType = entityType,
            entityId = entityId,
            timestamp = LocalDateTime.now(),
            isDeleted = false
        )
        repository.save(log)
    }
}