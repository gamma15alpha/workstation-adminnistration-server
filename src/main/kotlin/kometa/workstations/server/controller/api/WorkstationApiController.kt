package kometa.workstations.server.controller.api

import kometa.workstations.server.model.Workstation
import kometa.workstations.server.service.WorkstationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/workstations")
class WorkstationApiController(
    private val workstationService: WorkstationService
) {

    @GetMapping("/search")
    fun searchWorkstations(@RequestParam query: String): List<WorkstationResponse> {
        val workstations = workstationService.searchByNameOrInventory(query)
        return workstations.map { ws ->
            WorkstationResponse(
                id = ws.id ?: 0,
                name = ws.name,
                inventoryNumber = ws.inventoryNumber
            )
        }
    }

    @GetMapping("/searchByUserUUID")
    fun searchWorkstationsByUserUUID(@RequestParam username: String): List<Workstation> {
        return workstationService.findByAssignedUser(username)
    }

    data class WorkstationResponse(
        val id: Long,
        val name: String,
        val inventoryNumber: String
    )
}