package ru.stnk.resttestapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import ru.stnk.resttestapi.service.login.UserLoginForm
import ru.stnk.resttestapi.entity.User
import ru.stnk.resttestapi.repository.RolesRepository
import ru.stnk.resttestapi.repository.UserRepository
import ru.stnk.resttestapi.repository.VerificationCodeRepository
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(SpringExtension::class)
@SpringBootTest
class RegistrationControllerServiceTest {

//    @InjectMocks
//    val registrationControllerService: RegistrationControllerService? = null

    @Autowired
    val registrationControllerService: RegistrationControllerService? = null

    @MockBean
    val userRepository: UserRepository? = null

    @MockBean
    val rolesRepository: RolesRepository? = null

    @MockBean
    val verificationCodeRepository: VerificationCodeRepository? = null

    @MockBean
    val mailSender: MailSender? = null


    @Test
    fun injectMockBeans() {
        assertThat(userRepository).isNotNull
        assertThat(rolesRepository).isNotNull
        assertThat(verificationCodeRepository).isNotNull
        assertThat(mailSender).isNotNull
    }

    @Test
    fun testUserExist() {
        val mockUser: User = User()
        mockUser.email = "user@test.io"
        mockUser.phone = "88002000900"
        mockUser.password = "12345"

        `when`(userRepository?.existsByEmail("user@test.io")).thenReturn(true)
        val isUserExist: Boolean? = registrationControllerService?.userExists(mockUser.email)

        assertNotNull(isUserExist)
        assertTrue(isUserExist)

        verify(userRepository, times(1))?.existsByEmail(mockUser.email)
    }

    @Test
    fun testRegisterNewUserAccount() {
        val mockUser: UserLoginForm = UserLoginForm()
        mockUser.email = "user@test.io"
        mockUser.phone = "88002000900"
        mockUser.password = "12345"
        mockUser.os = "none"
        mockUser.isViaEmail = false

        `when`(userRepository?.save(ArgumentMatchers.any(User::class.java))).thenReturn(User())

        val newUserAccount: User? = registrationControllerService?.registerNewUserAccount(mockUser)

        assertNotNull(newUserAccount)

        verify(userRepository, times(1))?.save(ArgumentMatchers.any(User::class.java))
    }


}