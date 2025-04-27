package kometa.workstations.server.controller

import kometa.workstations.server.service.SoftwareService
import kometa.workstations.server.service.WorkstationService
import kometa.workstations.server.service.WorkstationSoftwareService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
@RequestMapping("/workstation-software")
class WorkstationSoftwareController(
    private val workstationSoftwareService: WorkstationSoftwareService,
    private val workstationService: WorkstationService,
    private val softwareService: SoftwareService
) {

    @GetMapping("/install")
    fun installForm(
        @RequestParam softwareId: Long,
        model: Model
    ): String {
        val software = softwareService.findById(softwareId) ?: throw IllegalArgumentException("ПО не найдено")
        model.addAttribute("software", software)
        return "install-software-form"
    }

    @PostMapping("/install")
    fun installSoftware(
        @RequestParam workstationId: Long,
        @RequestParam softwareId: Long,
        model: Model
    ): String {
        try {
            workstationSoftwareService.installSoftware(workstationId, softwareId)
            return "redirect:/workstations/view/$workstationId?success"
        } catch (e: Exception) {
            model.addAttribute("error", e.message)
            return "redirect:/workstation-software/install?workstationId=$workstationId&error=${e.message}"
        }
    }

    @GetMapping("/search")
    fun search(@RequestParam query: String): List<WorkstationDto> {
        val results = workstationService.searchByNameOrInventory(query)
        return results.map { WorkstationDto(it.id!!, it.name, it.inventoryNumber) }
    }

    data class WorkstationDto(
        val id: Long,
        val name: String,
        val inventoryNumber: String
    )
}
