package kometa.workstations.server.controller

import kometa.workstations.server.service.WorkstationService
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/workstations")
class WorkstationPanelController(
    private val workstationService: WorkstationService
) {

    @GetMapping
    fun panel(@RequestParam(required = false) search: String?, model: Model, auth: Authentication): String {
        val role = auth.authorities.map { it.authority }.toSet()
        val isEngineerOrAdmin = role.contains("ROLE_ENGINEER") || role.contains("ROLE_ADMIN")
        val isManager = role.contains("ROLE_MANAGER")

        val workstations = when {
            isEngineerOrAdmin -> workstationService.findAll()
            isManager -> workstationService.findAll()
            else -> emptyList()
        }.filter {
            search.isNullOrBlank() || it.name.contains(search, true) || it.inventoryNumber.contains(search, true)
        }

        model.addAttribute("workstations", workstations)
        return "workstation-panel"
    }
}
