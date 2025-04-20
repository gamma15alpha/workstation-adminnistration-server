package kometa.workstations.server.model

import jakarta.persistence.*
import lombok.Data

@Entity
@Table(name = "roles")
@Data
class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val name: String // ROLE_ADMIN, ROLE_ENGINEER, ROLE_MANAGER
) {
    constructor() : this(
        null,
        ""
    )
}