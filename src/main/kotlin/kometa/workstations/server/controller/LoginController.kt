package kometa.workstations.server.controller

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class LoginController(
    private val authenticationManager: AuthenticationManager
) {
    @GetMapping("/login")
    fun showLoginForm(
        @RequestParam(required = false) error: String?,
        @RequestParam(required = false) logout: String?,
        @RequestParam(required = false) registered: String?,
        model: Model
    ): String {
        if (error != null) {
            model.addAttribute("error", "Неверное имя пользователя или пароль")
        }
        if (logout != null) {
            model.addAttribute("logout", "Вы успешно вышли")
        }
        if (registered != null) {
            model.addAttribute("registered", "Регистрация успешна! Пожалуйста, войдите")
        }
        return "login"
    }

    @PostMapping("/login")
    fun processLogin(
        @RequestParam username: String,
        @RequestParam password: String,
        model: Model
    ): String {
        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(username, password)
            )
            SecurityContextHolder.getContext().authentication = authentication
            return "redirect:/admin/users"
        } catch (e: Exception) {
            model.addAttribute("error", "Неверное имя пользователя или пароль")
            return "login"
        }
    }
}