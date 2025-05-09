package kometa.workstations.server.model

import jakarta.persistence.*
import lombok.Data
import java.time.LocalDate

@Entity
@Table(name = "software")
@Data
class Software(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val version: String,

    @Column
    @Convert(converter = AesConverter::class)
    val licenseKey: String?,

    @Column
    val licenseCount: Int?,

    @Column
    val expirationDate: LocalDate?
){
    constructor() : this(
        id = null,
        name = "",
        version = "",
        licenseKey = null,
        licenseCount = null,
        expirationDate = null
    )
}