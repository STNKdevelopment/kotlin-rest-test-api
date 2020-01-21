package ru.stnk.resttestapi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.internal.verification.VerificationModeFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import ru.stnk.resttestapi.dto.UserDTO
import ru.stnk.resttestapi.entity.User
import ru.stnk.resttestapi.repository.UserRepository

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@AutoConfigureRestDocs(outputDir = "build/generated/documentation")
class RegistrationControllerTest (
        @Autowired val mockMvc: MockMvc
) {

    // Создаём фиктивный объект репозитория (таблицы) с пользователями
    // Только вот UserRepository используется в ControllerService, а ControllerService вызывается в RegistrationController
    // Бин UserRepository заменяется в контексте выполнения???
    // Any existing single bean of the same type defined in the context will be replaced by the mock. If no existing bean is defined a new one will be added.
    // Значит да, оставлю как памятку.
    // update: после перехода на Kotlin, из-за этого кода валится тест
    //@MockBean
    //val userRepository: UserRepository? = null

    private val objectMapper = ObjectMapper()

    //    @MockBean
    //    private ControllerService controllerService;
    //    @MockBean
    //    private RolesRepository rolesRepository;
    //    @MockBean
    //    private VerificationCodeRepository verificationCodeRepository;
    //    @InjectMocks
    //    private ControllerService controllerService;
    // Выполняет код перед каждым тестом
    /*@Before
        public void init() {
            rolesRepository.save(new Roles("ROLE_ADMIN"));
        }*/
    // Тест GET метода для получения кода подтверждения регистрации
    @Test
    @Order(1)
    @Throws(Exception::class)
    fun preRegistrationGetMethodTest() {
        /*
        User user = new User();
        user.setEmail("admin@test.io");
        user.setPassword("123");
        user.setPhone("88002000600");
        user.setOs("android");
        user.setEnabled(true);
        user.setEmailConfirmed(true);
        user.setBetBalance((long) 0);
        user.setFreeBalance((long) 0);
        user.setWithdrawalBalance((long) 0);
        user.setRoles(new ArrayList<>());
        user.addRole(rolesRepository.findByName("ROLE_USER"));

        when(userRepository.save(any(User.class))).thenReturn(user);
        */
        mockMvc.perform(MockMvcRequestBuilders.get("/reg-start")
                .param("email", "admin1@test.io")
                .param("password", "123")
                .param("phone", "88002000601")
                .param("os", "android")
                .param("viaEmail", "false")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.secondsUntilExpired", Matchers.isA<Any>(Int::class.java)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.secondsUntilResend", Matchers.isA<Any>(Int::class.java)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attempts", Matchers.isA<Any>(Int::class.java)))
                .andDo(MockMvcRestDocumentation.document("{methodName}", RequestDocumentation.requestParameters(
                        RequestDocumentation.parameterWithName("email").description("Email пользователя"),
                        RequestDocumentation.parameterWithName("password").description("Пароль пользователя"),
                        RequestDocumentation.parameterWithName("phone").description("Номер телефона пользователя"),
                        RequestDocumentation.parameterWithName("os").description("Используемая система пользователя"),
                        RequestDocumentation.parameterWithName("viaEmail").description("Отправлять ли письмо на указанный Email").optional()
                ), PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("error").description("Содержит код ошибки"),
                        PayloadDocumentation.fieldWithPath("description").description("Содержит описание ошибки"),
                        PayloadDocumentation.subsectionWithPath("data").description("Содержит данные запроса"),
                        PayloadDocumentation.fieldWithPath("data['secondsUntilExpired']").description("Время, через которое истечет проверочный код"),
                        PayloadDocumentation.fieldWithPath("data['secondsUntilResend']").description("Время, для повторного запроса проверочного кода"),
                        PayloadDocumentation.fieldWithPath("data['attempts']").description("Число попыток для ввода проверочного кода")
                )
                ))
        //verify(verificationCodeRepository, times(1)).save(any(VerificationCode.class));
        //verify(controllerService, times(1)).saveCheckCodeToEmail(anyString(), anyBoolean());
    }

    // Тест POST метода для получения кода подтверждения регистрации
    @Test
    @Order(3)
    @Throws(Exception::class)
    fun preRegistrationPostMethodTest() {
        val userDTO = UserDTO()
        userDTO.email = "admin2@test.io"
        userDTO.password = "123"
        userDTO.phone = "88002000602"
        userDTO.os = "android"
        userDTO.isViaEmail = false
        mockMvc.perform(MockMvcRequestBuilders.post("/reg-start")
                .content(objectMapper.writeValueAsString(userDTO))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.secondsUntilExpired", Matchers.isA<Any>(Int::class.java)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.secondsUntilResend", Matchers.isA<Any>(Int::class.java)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attempts", Matchers.isA<Any>(Int::class.java)))
        //verify(verificationCodeRepository, times(1)).save(any(VerificationCode.class));
        //verify(verificationCodeRepository, times(1)).save(any(VerificationCode.class));
    }

    // Тест GET метода для регистрации пользователя и получения Session_id
    @Test
    @Order(2)
    @Throws(Exception::class)
    fun registrationConfirmGetMethodTest() {
        mockMvc.perform(MockMvcRequestBuilders.get("/reg-confirm")
                .param("email", "admin1@test.io")
                .param("password", "123")
                .param("phone", "88002000601")
                .param("os", "android")
                .param("code", "9999")
                .param("viaEmail", "false")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.session_id", Matchers.isA<Any>(String::class.java)))
        //Mockito.verify(userRepository, VerificationModeFactory.times(1))?.save(ArgumentMatchers.any(User::class.java))
        //verify(verificationCodeRepository, times(1)).delete(any(VerificationCode.class));
    }

    // Тест POST метода для регистрации пользователя и получения Session_id
    @Test
    @Order(4)
    @Throws(Exception::class)
    fun registrationConfirmPostMethodTest() {
        val userDTO = UserDTO()
        userDTO.email = "admin2@test.io"
        userDTO.password = "123"
        userDTO.phone = "88002000602"
        userDTO.os = "android"
        userDTO.isViaEmail = false
        userDTO.code = "9999"
        mockMvc.perform(MockMvcRequestBuilders.post("/reg-confirm")
                .content(objectMapper.writeValueAsString(userDTO))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.session_id", Matchers.isA<Any>(String::class.java)))
        //Mockito.verify(userRepository, VerificationModeFactory.times(1))!!.save(ArgumentMatchers.any(User::class.java))
        //verify(verificationCodeRepository, times(1)).delete(any(VerificationCode.class));
    }

    // Выполняет этот код после каждого теста
    /*@After
    public void cleanup() {
        if (userRepository.findByEmail("admin1@test.io").isPresent()) {
            userRepository.delete(userRepository.findByEmail("admin1@test.io").get());
        }

        if (userRepository.findByEmail("admin2@test.io").isPresent()) {
            userRepository.delete(userRepository.findByEmail("admin2@test.io").get());
        }

    }*/
}