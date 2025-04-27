package kometa.workstations.server.controller

import kometa.workstations.server.model.Location
import kometa.workstations.server.service.LocationService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/locations")
class LocationController(
    private val locationService: LocationService
) {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun list(
        @RequestParam(defaultValue = "") search: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sortBy: String,
        @RequestParam(defaultValue = "asc") sortDir: String,
        model: Model
    ): String {
        val sort = Sort.by(if (sortDir == "asc") Sort.Direction.ASC else Sort.Direction.DESC, sortBy)
        val pageable = PageRequest.of(page, size, sort)
        val locationPage: Page<Location> = locationService.findByNamePaginated(search, pageable)

        model.addAttribute("locationPage", locationPage)
        model.addAttribute("currentPage", page)
        model.addAttribute("search", search)
        model.addAttribute("sortBy", sortBy)
        model.addAttribute("sortDir", sortDir)
        return "location-list"
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Long, model: Model): String {
        val location = locationService.findById(id) ?: throw IllegalArgumentException("Локация не найдена")
        model.addAttribute("location", location)
        return "location-form"
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    fun newLocation(model: Model): String {
        model.addAttribute("location", Location())
        return "location-form"
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    fun save(@ModelAttribute location: Location): String {
        locationService.save(location)
        return "redirect:/locations"
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Long): String {
        locationService.deleteById(id)
        return "redirect:/locations"
    }
}