package kometa.workstations.server.service

import kometa.workstations.server.model.Software
import kometa.workstations.server.model.WorkstationSoftware
import kometa.workstations.server.repository.SoftwareRepository
import kometa.workstations.server.repository.WorkstationRepository
import kometa.workstations.server.repository.WorkstationSoftwareRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class WorkstationSoftwareService(
    private val workstationSoftwareRepository: WorkstationSoftwareRepository,
    private val softwareRepository: SoftwareRepository,
    private val workstationRepository: WorkstationRepository
) {
    @Cacheable("workstationSoftware")
    fun findById(id: Long): WorkstationSoftware? {
        return workstationSoftwareRepository.findById(id).orElse(null)
    }

    @Cacheable("workstationSoftware")
    fun findAll(): List<WorkstationSoftware> {
        return workstationSoftwareRepository.findAll()
    }

    @Cacheable("workstationSoftware")
    fun findByWorkstationId(workstationId: Long): List<WorkstationSoftware> {
        return workstationSoftwareRepository.findByWorkstationId(workstationId)
    }

    @Cacheable("workstationSoftware")
    fun findBySoftwareId(softwareId: Long): List<WorkstationSoftware> {
        return workstationSoftwareRepository.findBySoftwareId(softwareId)
    }

    fun countBySoftware(software: Software): Long {
        return workstationSoftwareRepository.countBySoftware(software)
    }

    fun installSoftware(workstationId: Long, softwareId: Long): Boolean {
        val workstation = workstationRepository.findById(workstationId).orElseThrow { IllegalArgumentException("Станция не найдена") }
        val software = softwareRepository.findById(softwareId).orElseThrow { IllegalArgumentException("ПО не найдено") }

        if (workstationSoftwareRepository.existsByWorkstationAndSoftware(workstation, software)) {
            throw IllegalStateException("Это ПО уже установлено на данной станции")
        }

        if (software.licenseCount != null) {
            val used = workstationSoftwareRepository.countBySoftware(software)
            if (used >= software.licenseCount) {
                throw IllegalStateException("Недостаточно лицензий для установки")
            }
        }

        workstationSoftwareRepository.save(
            WorkstationSoftware(
                workstation = workstation,
                software = software,
                installationDate = LocalDate.now()
            )
        )

        return true
    }


    @Transactional
    @CacheEvict(value = ["workstationSoftware"], allEntries = true)
    fun save(workstationSoftware: WorkstationSoftware): WorkstationSoftware {
        return workstationSoftwareRepository.save(workstationSoftware)
    }

    @Transactional
    @CacheEvict(value = ["workstationSoftware"], allEntries = true)
    fun deleteById(id: Long) {
        workstationSoftwareRepository.deleteById(id)
    }
}