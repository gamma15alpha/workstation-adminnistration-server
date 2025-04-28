package kometa.workstations.server.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class BackupService(
    @Value("\${spring.datasource.url}") private val dbUrl: String,
    @Value("\${spring.datasource.username}") private val dbUser: String,
    @Value("\${spring.datasource.password}") private val dbPassword: String
) {

    private val backupDir = "backups"
    private val containerName = "postgres"
    private val dbName = dbUrl.substringAfterLast("/")

    init {
        val dir = File(backupDir)
        if (!dir.exists()) dir.mkdirs()
        println("PostgreSQL Backup Service Initialized. DB: $dbName")
    }

    /**
     * Создание бэкапа базы данных
     */
    fun createBackup(): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val backupFile = "$backupDir/$dbName-$timestamp.sql"

        val dumpCommand = listOf(
            "docker", "exec", containerName,
            "pg_dump", "-U", dbUser, "-d", dbName, "-F", "c", "-f", "/tmp/backup.sql"
        )

        val copyCommand = listOf(
            "docker", "cp", "$containerName:/tmp/backup.sql", backupFile
        )

        return try {
            ProcessBuilder(dumpCommand).start().waitFor()
            ProcessBuilder(copyCommand).start().waitFor()
            "Резервная копия создана: $backupFile"
        } catch (e: IOException) {
            "Error: ${e.message}"
        }
    }

    /**
     * Восстановление базы данных из бэкапа
     */
    fun restoreBackup(backupFileName: String): String {
        val filePath = "$backupDir/$backupFileName"

        if (!File(filePath).exists()) {
            return "Ошибка: файл резервной копии не найден"
        }

        val copyCommand = listOf(
            "docker", "cp", filePath, "$containerName:/tmp/restore.sql"
        )

        val restoreCommand = listOf(
            "docker", "exec", containerName,
            "pg_restore", "-U", dbUser, "-d", dbName, "-c", "/tmp/restore.sql"
        )

        return try {
            ProcessBuilder(copyCommand).start().waitFor()
            ProcessBuilder(restoreCommand).start().waitFor()
            "База данных восстановлена из: $backupFileName"
        } catch (e: IOException) {
            "Error: ${e.message}"
        }
    }

    /**
     * Получение списка доступных бэкапов
     */
    fun listBackups(): List<String> {
        return File(backupDir).listFiles()
            ?.map { it.name }
            ?.sortedDescending()
            ?: emptyList()
    }

    /**
     * Удалить бэкап
     */
    fun deleteBackup(backupFileName: String): String {
        val filePath = "$backupDir/$backupFileName"

        val file = File(filePath)
        if (!file.exists()) {
            return "Ошибка: файл резервной копии не найден"
        }

        return if (file.delete()) {
            "Резервная копия $backupFileName удалена"
        } else {
            "Ошибка: не удалось удалить резервную копию $backupFileName"
        }
    }

}
