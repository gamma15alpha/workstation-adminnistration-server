package kometa.workstations.server.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class DataExportImportService(
    @Value("\${spring.datasource.url}") private val dbUrl: String,
    @Value("\${spring.datasource.username}") private val dbUser: String,
    @Value("\${spring.datasource.password}") private val dbPassword: String
) {

    private val exportDir = "exports"
    private val importDir = "imports"
    private val containerName = "postgres" // Имя контейнера PostgreSQL
    private val dbName = dbUrl.substringAfterLast("/")

    init {
        var dir = File(exportDir)
        if (!dir.exists()) dir.mkdirs()
        dir = File(importDir)
        if (!dir.exists()) dir.mkdirs()
    }

    /**
     * Экспорт таблицы в SQL
     */
    fun exportTableToSql(tableName: String): FileSystemResource {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val exportFile = "$exportDir/${tableName}_$timestamp.sql"

        // Команда для экспорта только указанной таблицы в SQL
        val dumpCommand = listOf(
            "docker", "exec", containerName,
            "pg_dump", "-U", dbUser, "-d", dbName, "-t", tableName, "-F", "p", "-f", "/tmp/$tableName.sql"
        )

        val copyCommand = listOf(
            "docker", "cp", "$containerName:/tmp/$tableName.sql", exportFile
        )

        return try {
            // Выполняем команду экспорта
            ProcessBuilder(dumpCommand).start().waitFor()
            // Копируем файл в локальную файловую систему
            ProcessBuilder(copyCommand).start().waitFor()
            FileSystemResource(File(exportFile))
        } catch (e: IOException) {
            throw RuntimeException("Error SQL exporting: ${e.message}")
        }
    }

    /**
     * Импорт данных в таблицу из SQL (только для одной таблицы)
     */
    fun importTableFromSql(tableName: String, file: MultipartFile): String {
        val tempFile = File(importDir, file.originalFilename ?: "$tableName-import.sql")
        tempFile.outputStream().use { file.inputStream.copyTo(it) }

        // Проверяем, что файл успешно сохранился
        if (!tempFile.exists()) {
            return "File not saved (${tempFile.absolutePath})"
        }

        // Копируем файл SQL в контейнер Docker
        val copyCommand = listOf(
            "docker", "cp", tempFile.absolutePath, "$containerName:/tmp/$tableName.sql"
        )

        // Команда для восстановления данных из SQL-файла
        val restoreCommand = listOf(
            "docker", "exec", containerName,
            "psql", "-U", dbUser, "-d", dbName, "-f", "/tmp/$tableName.sql"
        )

        return try {

            ProcessBuilder(copyCommand).start().waitFor()


            ProcessBuilder(restoreCommand).start().waitFor()


            if (tempFile.delete()) {
                "Table $tableName import successful"
            } else {
                "Table $tableName import successful"
            }
        } catch (e: IOException) {
            "Error SQL import: ${e.message}"
        }
    }

    /**
     * Экспорт таблицы в CSV
     */
    fun exportTableToCsv(tableName: String): FileSystemResource {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val exportFile = "$exportDir/${tableName}_$timestamp.csv"

        val exportCommand = listOf(
            "docker", "exec", containerName,
            "psql", "-U", dbUser, "-d", dbName, "-c",
            """\copy $tableName TO '/tmp/$tableName.csv' WITH CSV HEADER"""
        )

        val copyCommand = listOf(
            "docker", "cp", "$containerName:/tmp/$tableName.csv", exportFile
        )

        return try {
            ProcessBuilder(exportCommand).start().waitFor()
            ProcessBuilder(copyCommand).start().waitFor()
            FileSystemResource(File(exportFile))
        } catch (e: IOException) {
            throw RuntimeException("Error CSV export: ${e.message}")
        }
    }

    /**
     * Импорт данных в таблицу из CSV
     */
    fun importTableFromCsv(tableName: String, file: MultipartFile): String {
        val tempFile = File(exportDir, file.originalFilename ?: "$tableName-import.csv")
        tempFile.outputStream().use { file.inputStream.copyTo(it) }

        if (!tempFile.exists()) {
            return "File not saved (${tempFile.absolutePath})"
        }

        val copyCommand = listOf(
            "docker", "cp", tempFile.absolutePath, "$containerName:/tmp/$tableName.csv"
        )

        val importCommand = listOf(
            "docker", "exec", containerName,
            "psql", "-U", dbUser, "-d", dbName, "-c",
            """\copy $tableName FROM '/tmp/$tableName.csv' WITH CSV HEADER"""
        )

        return try {
            ProcessBuilder(copyCommand).start().waitFor()
            ProcessBuilder(importCommand).start().waitFor()
            "CSV imported in $tableName from ${tempFile.name}"
        } catch (e: IOException) {
            "Error in CSV import: ${e.message}"
        }
    }
}
