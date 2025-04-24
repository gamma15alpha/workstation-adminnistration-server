package kometa.workstations.server.model

import jakarta.persistence.*
import lombok.Data

@Entity
@Table(name = "hardware_components")
@Data
class HardwareComponent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workstation_id", nullable = false)
    val workstation: Workstation,

    @Column(nullable = false)
    val type: String,

    @Column(nullable = false)
    val model: String,

    @Column
    val serialNumber: String?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ComponentStatus
){
    constructor() : this(
        id = null,
        workstation = Workstation(),
        type = "",
        model = "",
        serialNumber = null,
        status = ComponentStatus.INACTIVE
    )
}

enum class ComponentStatus { ACTIVE, INACTIVE, BROKEN }