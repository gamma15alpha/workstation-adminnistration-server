package kometa.workstations.server

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
class WorkstationsApplication

fun main(args: Array<String>) {
    dotenv()
    runApplication<WorkstationsApplication>(*args)
}

