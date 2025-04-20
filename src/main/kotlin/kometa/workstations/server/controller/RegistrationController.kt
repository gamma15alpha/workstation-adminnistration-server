package kometa.workstations.server.controller


import kometa.workstations.server.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class RegistrationController(private val userService: UserService) {
    @GetMapping("/register")
    fun showRegistrationForm(): String {
        return "register"
    }

    @PostMapping("/register")
    fun registerUser(
        @RequestParam username: String,
        @RequestParam password: String,
        @RequestParam confirmPassword: String,
        model: Model
    ): String {
        if (password != confirmPassword) {
            model.addAttribute("error", "Пароли не совпадают")
            return "register"
        }

        try {
            userService.registerUser(username, password)
            return "redirect:/login?registered=true"
        } catch (e: IllegalArgumentException) {
            model.addAttribute("error", e.message)
            return "register"
        }
    }
}