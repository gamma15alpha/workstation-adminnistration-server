package kometa.workstations.server.controller

import kometa.workstations.server.model.User
import kometa.workstations.server.repository.RoleRepository
import kometa.workstations.server.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
class AdminUserController(private val userService: UserService, private val roleRepository: RoleRepository) {
    @GetMapping
    fun listUsers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) enabled: String?,
        @RequestParam(required = false) role: String?,
        @RequestParam(defaultValue = "id") sortBy: String,
        @RequestParam(defaultValue = "asc") sortDir: String,
        model: Model
    ): String {
        val sort = Sort.by(if (sortDir == "asc") Sort.Direction.ASC else Sort.Direction.DESC, sortBy)
        val pageable = PageRequest.of(page, size, sort)
        val userPage: Page<User> = userService.findFiltered(
            username,
            enabled,
            role,
            pageable
        )
        model.addAttribute ("users", userPage)
        model.addAttribute("currentPage", page)
        model.addAttribute("totalPages", userPage.totalPages)
        model.addAttribute("availableRoles", roleRepository.findAll())
        return "admin/user-list"
    }

    @GetMapping("/new")
    fun newUser(model: Model): String {
        model.addAttribute("user", User(username = "", password = "", enabled = true))
        model.addAttribute("availableRoles", roleRepository.findAll())
        return "admin/user-form"
    }

    @GetMapping("/edit/{id}")
    fun editUser(@PathVariable id: Long, model: Model): String {
        val user = userService.findById(id) ?: throw IllegalArgumentException("Пользователь не найден")
        model.addAttribute("user", user)
        model.addAttribute("availableRoles", roleRepository.findAll())
        return "admin/user-form"
    }

    @PostMapping("/save")
    fun saveUser(@ModelAttribute("user") user: User): String {
        userService.save(user)
        return "redirect:/admin/users"
    }

    @PostMapping("/delete/{id}")
    fun deleteUser(@PathVariable id: Long): String {
        userService.deleteById(id)
        return "redirect:/admin/users"
    }
}