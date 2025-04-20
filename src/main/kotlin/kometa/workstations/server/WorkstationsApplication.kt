package kometa.workstations.server

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WorkstationsApplication

fun main(args: Array<String>) {
    dotenv()
    runApplication<WorkstationsApplication>(*args)
}

