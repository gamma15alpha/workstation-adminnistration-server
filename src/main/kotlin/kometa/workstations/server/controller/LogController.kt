package kometa.workstations.server.controller

import kometa.workstations.server.service.LogService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/logs")
class LogController(private val logService: LogService) {

    @GetMapping
    fun listLogs(
        @RequestParam(defaultValue = "") search: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        model: Model
    ): String {
        val pageable = PageRequest.of(page, size)
        val logsPage = logService.findLogsPaginated(search, pageable)
        model.addAttribute("logsPage", logsPage)
        model.addAttribute("currentPage", page)
        model.addAttribute("search", search)
        return "log-list"
    }
}
