package ru.stnk.resttestapi.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(
        name = "users",
        uniqueConstraints = arrayOf(
                UniqueConstraint(columnNames = ["email"]),
                UniqueConstraint(columnNames = ["email"])
        )
)
class User : AuditModel(), Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    var id: Long? = null

    @Column(name = "email")
    var email: String = ""

    @Column(name = "phone")
    var phone: String = ""

    @Column(name = "password")
    @JsonIgnore
    var password: String = ""
        set(value) {
            field = passwordEncoder(value)
        }

    @Column(name = "email_confirmed")
    var emailConfirmed: Boolean = false

    @Column(name = "os")
    @JsonIgnore
    var os: String = "none"

    @Column(name = "free_balance")
    var freeBalance: Long = 0

    @Column(name = "bet_balance")
    var betBalance: Long = 0

    @Column(name = "withdrawal_balance")
    var withdrawalBalance: Long = 0

    @Column(name = "enabled")
    @JsonIgnore
    var isEnabled: Boolean = false

    /*
* По умолчанию ManyToMany(fetch = FetchType.LAZY) получается ошибка
* org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role
*
* если оставить fetch = FetchType.EAGER , то ошибки нет
*
* почему?????!!!! Подумать над решением с fetch = FetchType.LAZY
*
* */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = arrayOf(
                    JoinColumn(name = "user_id", referencedColumnName = "id"),
                    JoinColumn(name = "user_email", referencedColumnName = "email")
            ),
            inverseJoinColumns = arrayOf(
                    JoinColumn(name = "role_id", referencedColumnName = "id"),
                    JoinColumn(name = "role_name", referencedColumnName = "name")
            )
    )
    @JsonIgnore
    var roles: MutableList<Role> = ArrayList()

    private fun passwordEncoder(password: String): String {
        return BCryptPasswordEncoder().encode(password)
    }

    /*fun getFreeBalance(): Long? {
        return freeBalance
    }

    fun setFreeBalance(freeBalance: Long?) {
        this.freeBalance = freeBalance!!
    }

    fun getBetBalance(): Long? {
        return betBalance
    }

    fun setBetBalance(betBalance: Long?) {
        this.betBalance = betBalance!!
    }

    fun getWithdrawalBalance(): Long? {
        return withdrawalBalance
    }

    fun setWithdrawalBalance(withdrawalBalance: Long?) {
        this.withdrawalBalance = withdrawalBalance!!
    }

    fun getRoles(): List<RoleName>? {
        return roles
    }

    fun setRoles(roles: MutableList<RoleName>) {
        this.roles = roles
    }

    fun addRole(name: RoleName) {
        roles.add(name)
    }*/
}