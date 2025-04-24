package kometa.workstations.server.controller

import kometa.workstations.server.model.Software
import kometa.workstations.server.service.SoftwareService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/software")
class SoftwareController(
    private val softwareService: SoftwareService
) {

    @GetMapping
    fun list(@RequestParam(required = false) search: String?, model: Model): String {
        val softwareList = if (search.isNullOrBlank())
            softwareService.findAll()
        else
            softwareService.findByName(search)
        model.addAttribute("softwareList", softwareList)
        return "software-list"
    }

    @GetMapping("/new")
    fun newSoftware(model: Model): String {
        model.addAttribute("software", Software())
        return "software-form"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Long, model: Model): String {
        val software = softwareService.findById(id) ?: throw IllegalArgumentException("ПО не найдено")
        model.addAttribute("software", software)
        return "software-form"
    }

    @PostMapping("/save")
    fun save(@ModelAttribute software: Software): String {
        softwareService.save(software)
        return "redirect:/software"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Long): String {
        softwareService.deleteById(id)
        return "redirect:/software"
    }

    @GetMapping("/view/{id}")
    fun view(@PathVariable id: Long, model: Model): String {
        val software = softwareService.findById(id) ?: throw IllegalArgumentException("ПО не найдено")
        model.addAttribute("software", software)
        return "software-view"
    }
}
