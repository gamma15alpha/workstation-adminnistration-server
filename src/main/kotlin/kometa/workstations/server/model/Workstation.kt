package kometa.workstations.server.model

import jakarta.persistence.*
import lombok.Data
import java.time.LocalDateTime

@Entity
@Table(name = "workstations")
@Data
class Workstation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(unique = true, nullable = false)
    val inventoryNumber: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    val location: Location,

    @Column(nullable = false)
    val assignedUserUid: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
){
    constructor() : this(
        id = null,
        name = "",
        inventoryNumber = "",
        location = Location(),
        assignedUserUid = "",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

}