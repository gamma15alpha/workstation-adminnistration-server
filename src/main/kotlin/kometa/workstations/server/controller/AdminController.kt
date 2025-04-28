package kometa.workstations.server.controller

import kometa.workstations.server.service.BackupService
import kometa.workstations.server.service.DataExportImportService
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping("/admin")
class AdminController(
    private val dataExportImportService: DataExportImportService,
    private val backupService: BackupService
) {

    // Страница экспорта таблицы
    @GetMapping("/export-table")
    fun exportTablePage(model: Model): String {
        model.addAttribute("tables", listOf("workstations", "users", "software"))  // Пример таблиц
        return "export-table"  // Шаблон для экспорта таблиц
    }

    // Обработка запроса на экспорт таблицы
    @PostMapping("/export-table")
    fun exportTable(@RequestParam tableName: String): ResponseEntity<Resource> {
        val file = dataExportImportService.exportTableToSql(tableName)  // Экспорт в SQL
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${file.filename}")
            .body(file)
    }

    // Страница импорта таблицы
    @GetMapping("/import-table")
    fun importTablePage(model: Model): String {
        model.addAttribute("tables", listOf("workstations", "users", "software"))  // Пример таблиц
        return "import-table"  // Шаблон для импорта таблиц
    }

    // Обработка запроса на импорт таблицы
    @PostMapping("/import-table")
    fun importTable(@RequestParam tableName: String, @RequestParam file: MultipartFile): String {
        val message = dataExportImportService.importTableFromSql(tableName, file)
        return "redirect:/admin/import-table?message=$message"
    }

    // Страница бэкапов
    @GetMapping("/backups")
    fun backupsPage(model: Model): String {
        model.addAttribute("backups", backupService.listBackups())
        return "backups"  // Шаблон для отображения бэкапов
    }

    // Создание бэкапа
    @PostMapping("/create-backup")
    fun createBackup(): String {
        val message = backupService.createBackup()
        return "redirect:/admin/backups?message=$message"
    }

    // Восстановление бэкапа
    @PostMapping("/restore-backup")
    fun restoreBackup(@RequestParam backupFileName: String): String {
        val message = backupService.restoreBackup(backupFileName)
        return "redirect:/admin/backups?message=$message"
    }

    // Удаление бэкапа
    @PostMapping("/delete-backup")
    fun deleteBackup(@RequestParam backupFileName: String): String {
        val message = backupService.deleteBackup(backupFileName)
        return "redirect:/admin/backups?message=$message"
    }
}
