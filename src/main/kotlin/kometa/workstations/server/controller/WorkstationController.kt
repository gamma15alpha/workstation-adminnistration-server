package kometa.workstations.server.controller

import kometa.workstations.server.model.Workstation
import kometa.workstations.server.service.WorkstationService
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/workstations")
class WorkstationController(
    private val workstationService: WorkstationService
) {

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "") search: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        authentication: Authentication,
        model: Model
    ): String {
        val pageable = PageRequest.of(page, size)

        val isAdmin = authentication.authorities.any { it.authority == "ROLE_ADMIN" }
        val isEngineer = authentication.authorities.any { it.authority == "ROLE_ENGINEER" }
        val isManager = authentication.authorities.any { it.authority == "ROLE_MANAGER" }

        val allStations = when {
            isAdmin || isManager -> if (search.isBlank()) workstationService.findAll() else workstationService.searchByNameOrInventory(search)
            isEngineer -> workstationService.findByAssignedUser(authentication.name)
            else -> emptyList()
        }

        val fromIndex = page * size
        val toIndex = Integer.min(fromIndex + size, allStations.size)
        val pageContent = if (fromIndex > allStations.size) emptyList() else allStations.subList(fromIndex, toIndex)

        model.addAttribute("workstationPage", PageImpl(pageContent, pageable, allStations.size.toLong()))
        model.addAttribute("currentPage", page)
        model.addAttribute("search", search)
        model.addAttribute("isAdmin", isAdmin)
        model.addAttribute("isEngineer", isEngineer)

        return "workstation-list"
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @GetMapping("/new")
    fun newWorkstation(model: Model): String {
        model.addAttribute("workstation", Workstation())
        return "workstation-form"
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @GetMapping("/edit/{id}")
    fun editWorkstation(@PathVariable id: Long, authentication: Authentication, model: Model): String {
        val workstation = workstationService.findById(id) ?: throw IllegalArgumentException("Станция не найдена")

        val isAdmin = authentication.authorities.any { it.authority == "ROLE_ADMIN" }
        val isEngineerOwn = authentication.name == workstation.assignedUserUid

        if (!isAdmin && !isEngineerOwn) {
            throw IllegalAccessException("Нет прав на редактирование этой станции")
        }

        model.addAttribute("workstation", workstation)
        return "workstation-form"
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @PostMapping("/save")
    fun saveWorkstation(@ModelAttribute workstation: Workstation): String {
        workstationService.save(workstation)
        return "redirect:/workstations"
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    fun deleteWorkstation(@PathVariable id: Long): String {
        workstationService.deleteById(id)
        return "redirect:/workstations"
    }
}
