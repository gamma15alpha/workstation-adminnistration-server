package kometa.workstations.server.model

import jakarta.persistence.*
import lombok.Data

@Entity
@Table(name = "users")
@Data
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false)
    var password: String, // Хранится в зашифрованном виде (BCrypt)

    @Column(nullable = false)
    val enabled: Boolean = true,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: Set<Role> = emptySet()
) {
    constructor() : this(
        id = null,
        username = "",
        password = "",
        enabled = true,
        roles = emptySet(),
    )
}