package kometa.workstations.server.controller

import kometa.workstations.server.model.ComponentStatus
import kometa.workstations.server.model.HardwareComponent
import kometa.workstations.server.service.HardwareComponentService
import kometa.workstations.server.service.WorkstationService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/hardware-components")
class HardwareComponentController(
    private val componentService: HardwareComponentService,
    private val workstationService: WorkstationService
) {
    @GetMapping
    fun list(
        @RequestParam(defaultValue = "") search: String,
        @RequestParam(required = false) type: String?,
        @RequestParam(required = false) status: String?, // теперь String а не ComponentStatus
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sortBy: String,
        @RequestParam(defaultValue = "asc") sortDir: String,
        model: Model
    ): String {
        val sort = Sort.by(if (sortDir == "asc") Sort.Direction.ASC else Sort.Direction.DESC, sortBy)
        val pageable = PageRequest.of(page, size, sort)

        val componentsPage = when {
            search.isNotBlank() -> componentService.findByModelOrSerialNumberPaginated(search, pageable)
            type != null && status != null -> {
                val realStatus = ComponentStatus.entries.find { it.displayName == status }
                if (realStatus != null)
                    componentService.findByTypeAndStatusPaginated(type, realStatus, pageable)
                else
                    componentService.findAllPaginated(pageable)
            }
            else -> componentService.findAllPaginated(pageable)
        }

        model.addAttribute("componentsPage", componentsPage)
        model.addAttribute("currentPage", page)
        model.addAttribute("search", search)
        model.addAttribute("type", type)
        model.addAttribute("status", status)
        model.addAttribute("sortBy", sortBy)
        model.addAttribute("sortDir", sortDir)
        model.addAttribute("allStatuses", ComponentStatus.values())
        return "hardware-component-list"
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    fun newComponent(model: Model): String {
        model.addAttribute("component", HardwareComponent())
        model.addAttribute("workstations", workstationService.findAll())
        model.addAttribute("statuses", ComponentStatus.entries.toTypedArray())
        return "hardware-component-form"
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    fun editComponent(@PathVariable id: Long, model: Model): String {
        val component = componentService.findById(id) ?: throw IllegalArgumentException("Компонент не найден")
        model.addAttribute("component", component)
        model.addAttribute("workstations", workstationService.findAll())
        model.addAttribute("statuses", ComponentStatus.values())
        model.addAttribute("isAdmin", true)
        return "hardware-component-form"
    }

    @PreAuthorize("hasRole('ENGINEER')")
    @GetMapping("/edit-status/{id}")
    fun editComponentStatus(@PathVariable id: Long, model: Model): String {
        val component = componentService.findById(id) ?: throw IllegalArgumentException("Компонент не найден")
        model.addAttribute("component", component)
        model.addAttribute("statuses", ComponentStatus.values())
        model.addAttribute("isAdmin", false)
        return "hardware-component-form"
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    fun saveComponent(@ModelAttribute component: HardwareComponent): String {
        componentService.save(component)
        return "redirect:/hardware-components"
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    fun deleteComponent(@PathVariable id: Long): String {
        componentService.deleteById(id)
        return "redirect:/hardware-components"
    }
}
