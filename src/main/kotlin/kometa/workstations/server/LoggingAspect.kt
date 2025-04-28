package kometa.workstations.server

import kometa.workstations.server.service.LogService
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggingAspect(private val logService: LogService) {

    @Before("@annotation(org.springframework.web.bind.annotation.PostMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    fun logActionBefore(joinPoint: JoinPoint) {
        val methodName = joinPoint.signature.name
        val args = joinPoint.args


        val username = SecurityContextHolder.getContext().authentication?.name ?: "Unknown"


        val action = when (methodName) {
            "save" -> "Создание объекта"
            "delete" -> "Удаление объекта"
            "update" -> "Обновление объекта"
            else -> "Неизвестное действие"
        }


        val entityType = args.firstOrNull()?.javaClass?.simpleName
        val entityId = try {
            args.firstOrNull()?.let {

                it.javaClass.getDeclaredMethod("getId").invoke(it) as? Long
            }
        } catch (e: Exception) {
            0
        }


        logService.logAction(username, action, entityType, entityId)
    }
}
