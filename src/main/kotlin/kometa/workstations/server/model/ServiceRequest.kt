package kometa.workstations.server.model

import jakarta.persistence.*
import lombok.Data
import java.time.LocalDateTime

@Entity
@Table(name = "service_requests")
@Data
class ServiceRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workstation_id")
    var workstation: Workstation? = null,

    @Column(nullable = false)
    val requesterUid: String,

    @Column
    val assignedEngineerUid: String?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: RequestStatus,

    @Column
    val description: String?,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column
    val updatedAt: LocalDateTime?
){
    constructor() : this (
        id = null,
        workstation = Workstation(),
        requesterUid = "",
        assignedEngineerUid = "",
        status = RequestStatus.CLOSED,
        description = "",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
}

enum class RequestStatus { OPEN, IN_PROGRESS, COMPLETED, CLOSED }