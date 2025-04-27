package kometa.workstations.server.controller.api

import kometa.workstations.server.repository.LocationRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/locations")
class LocationApiController(
    private val locationRepository: LocationRepository
) {

    @GetMapping("/search")
    fun searchLocations(@RequestParam query: String): List<LocationDto> {
        val locations = locationRepository.findByNameContainingIgnoreCase(query)
        return locations.map { LocationDto(it.id!!, it.name) }
    }

    data class LocationDto(
        val id: Long,
        val name: String
    )
}
