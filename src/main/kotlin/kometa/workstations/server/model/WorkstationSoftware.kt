package kometa.workstations.server.model

import jakarta.persistence.*
import lombok.Data
import java.time.LocalDate

@Entity
@Table(name = "workstation_software")
@Data
class WorkstationSoftware(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workstation_id", nullable = false)
    val workstation: Workstation,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "software_id", nullable = false)
    val software: Software,

    @Column
    val installationDate: LocalDate?
){
    constructor() : this(
        id = null,
        workstation = Workstation(),
        software = Software(),
        installationDate = LocalDate.now()
    )
}