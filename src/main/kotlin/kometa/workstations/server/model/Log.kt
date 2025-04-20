package kometa.workstations.server.model

import jakarta.persistence.*
import lombok.Data
import java.time.LocalDateTime

@Entity
@Table(name = "logs")
@Data
class Log(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userLdapUid: String,

    @Column(nullable = false)
    val action: String,

    @Column
    val entityType: String?,

    @Column
    val entityId: Long?,

    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),

    @Column
    val isDeleted: Boolean = false
)