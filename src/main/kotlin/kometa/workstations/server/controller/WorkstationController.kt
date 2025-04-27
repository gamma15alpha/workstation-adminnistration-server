package kometa.workstations.server.controller

import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.io.ByteArrayOutputStream
import kometa.workstations.server.model.Workstation
import kometa.workstations.server.repository.LocationRepository
import kometa.workstations.server.service.HardwareComponentService
import kometa.workstations.server.service.WorkstationService
import kometa.workstations.server.service.WorkstationSoftwareService
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/workstations")
class WorkstationController(
    private val workstationService: WorkstationService,
    private val locationRepository: LocationRepository,
    private val hardwareComponentService: HardwareComponentService,
    private val workstationSoftwareService: WorkstationSoftwareService
) {

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "") search: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        authentication: Authentication,
        model: Model
    ): String {
        val pageable = PageRequest.of(page, size)

        val isAdmin = authentication.authorities.any { it.authority == "ROLE_ADMIN" }
        val isEngineer = authentication.authorities.any { it.authority == "ROLE_ENGINEER" }
        val isManager = authentication.authorities.any { it.authority == "ROLE_MANAGER" }

        val allStations = when {
            isAdmin || isEngineer || isManager -> if (search.isBlank()) workstationService.findAll()
            else workstationService.searchByNameOrInventory(search)
            else -> emptyList()
        }

        val fromIndex = page * size
        val toIndex = Integer.min(fromIndex + size, allStations.size)
        val pageContent = if (fromIndex > allStations.size) emptyList() else allStations.subList(fromIndex, toIndex)

        model.addAttribute("workstationPage", PageImpl(pageContent, pageable, allStations.size.toLong()))
        model.addAttribute("currentPage", page)
        model.addAttribute("search", search)
        model.addAttribute("isAdmin", isAdmin)
        model.addAttribute("isEngineer", isEngineer)

        return "workstation-list"
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @GetMapping("/new")
    fun newWorkstation(model: Model): String {
        model.addAttribute("workstation", Workstation())
        return "workstation-form"
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @GetMapping("/edit/{id}")
    fun editWorkstation(@PathVariable id: Long, authentication: Authentication, model: Model): String {
        val workstation = workstationService.findById(id) ?: throw IllegalArgumentException("Станция не найдена")

        val isAdmin = authentication.authorities.any { it.authority == "ROLE_ADMIN" }
        val isEngineerOwn = authentication.name == workstation.assignedUserUid

        if (!isAdmin && !isEngineerOwn) {
            throw IllegalAccessException("Нет прав на редактирование этой станции")
        }

        model.addAttribute("workstation", workstation)
        return "workstation-form"
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @PostMapping("/save")
    fun saveWorkstation(@ModelAttribute workstationForm: WorkstationForm): String {
        val location = workstationForm.locationId.let { locationRepository.findById(it).orElse(null) }
        val workstation = Workstation(
            id = workstationForm.id,
            name = workstationForm.name,
            inventoryNumber = workstationForm.inventoryNumber,
            assignedUserUid = workstationForm.assignedUserUid,
            location = location
        )
        workstationService.save(workstation)
        return "redirect:/workstations"
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    fun deleteWorkstation(@PathVariable id: Long): String {
        workstationService.deleteById(id)
        return "redirect:/workstations"
    }

    data class WorkstationForm(
        val id: Long? = null,
        val name: String,
        val inventoryNumber: String,
        val assignedUserUid: String,
        val locationId: Long
    )

    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'MANAGER')")
    @GetMapping("/view/{id}")
    fun viewWorkstation(@PathVariable id: Long, model: Model): String {
        val workstation = workstationService.findById(id) ?: throw IllegalArgumentException("Станция не найдена")

        val components = hardwareComponentService.findByWorkstationId(id)
        val installedSoftware = workstationSoftwareService.findByWorkstationId(id)

        model.addAttribute("workstation", workstation)
        model.addAttribute("components", components)
        model.addAttribute("installedSoftware", installedSoftware)

        return "workstation-view"
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'MANAGER')")
    @GetMapping("/pdf/{id}")
    fun generateWorkstationPdf(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val workstation = workstationService.findById(id) ?: throw IllegalArgumentException("Станция не найдена")
        val components = hardwareComponentService.findByWorkstationId(id)
        val installedSoftware = workstationSoftwareService.findByWorkstationId(id)

        // Создаем PDF документ
        val byteArrayOutputStream = ByteArrayOutputStream()
        val pdfWriter = PdfWriter(byteArrayOutputStream)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        // Подключаем шрифт, поддерживающий кириллицу
        val fontPath = "src/main/resources/fonts/ArialUnicodeMS.ttf" // Укажи путь к своему шрифту
        val font: PdfFont = PdfFontFactory.createFont(fontPath, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED)

        // Используем шрифт для всего текста в документе
        document.setFont(font)

        // Заголовок документа
        document.setTextAlignment(TextAlignment.CENTER)
        document.add(Paragraph("Корпорация \"Комета\"").setFontSize(18f))
        document.add(Paragraph("ОТЧЕТ О КОНФИГУРАЦИИ РАБОЧЕЙ СТАНЦИИ"))
        document.add(Paragraph("Наименование рабочей станции: ${workstation.name}"))
        document.add(Paragraph("Инвентарный номер: ${workstation.inventoryNumber}"))
        document.add(Paragraph("Ответственный пользователь: ${workstation.assignedUserUid}"))
        document.add(Paragraph("Локация: ${workstation.location.name}"))
        document.add(Paragraph("Дата создания: ${workstation.createdAt}"))
        document.add(Paragraph("Дата обновления: ${workstation.updatedAt}"))

        // Добавляем компоненты
        document.add(Paragraph("Компоненты:"))
        val componentsTable = Table(4)  // Количество столбцов: 4 (Тип, Модель, Серийный номер, Статус)
        componentsTable.addCell("Тип")
        componentsTable.addCell("Модель")
        componentsTable.addCell("Серийный номер")
        componentsTable.addCell("Статус")
        components.forEach { component ->
            componentsTable.addCell(component.type)
            componentsTable.addCell(component.model)
            componentsTable.addCell(component.serialNumber ?: "-")
            componentsTable.addCell(component.status.displayName)
        }
        document.add(componentsTable)

        // Добавляем установленное ПО
        document.add(Paragraph("Установленное ПО:"))
        val softwareTable = Table(2)
        softwareTable.addCell("Название")
        softwareTable.addCell("Версия")
        installedSoftware.forEach { software ->
            softwareTable.addCell(software.software.name)
            softwareTable.addCell(software.software.version)
        }
        document.add(softwareTable)

        // Подписи и утверждения
        document.add(Paragraph("Утверждаю:"))
        document.add(Paragraph("__________________________________"))
        document.add(Paragraph("Подпись: _________________________"))
        document.add(Paragraph("Дата: ___________________________"))

        document.close()

        val headers = HttpHeaders()
        headers.add("Content-Disposition", "attachment; filename=workstation_${workstation.id}.pdf")

        return ResponseEntity(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK)
    }
}
