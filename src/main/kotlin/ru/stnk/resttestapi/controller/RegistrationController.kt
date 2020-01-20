package ru.stnk.resttestapi.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import ru.stnk.resttestapi.dto.UserDTO
import ru.stnk.resttestapi.exception.registration.*
import ru.stnk.resttestapi.results.RestResponse
import ru.stnk.resttestapi.service.ControllerService
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.validation.Validation

@RestController
class RegistrationController {

    /*@Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private MailSender mailSender;*/

    @Autowired
    private var controllerService: ControllerService? = null

    /*
     *
     * Обработчики GET и POST запросов /reg-start
     *
     * используются для генерации проверочного кода для следующего метода
     * */

    @GetMapping("/reg-start")
    @Throws(IncorrectEmailException::class, LoginPasswordEqualException::class, IncorrectPasswordException::class, IncorrectPhoneException::class, DelayException::class)
    fun preRegistrationGetMethod(
            @RequestParam email: String,
            @RequestParam password: String,
            @RequestParam phone: String,
            @RequestParam os: String,
            @RequestParam(required = false, defaultValue = "true") viaEmail: String
    ): RestResponse {

        val response = RestResponse()

        //HashMap<String, Object> data = new HashMap<>();

        // Пароль не должен быть email-ом
        if (password == email) {
            throw LoginPasswordEqualException()
        }

        val userDTO = UserDTO()
        userDTO.email = email
        userDTO.password = password
        userDTO.phone = phone
        userDTO.os = os
        userDTO.isViaEmail = viaEmail == "true"

        /*
        * Про Validator взял отсюда
        * https://www.javaquery.com/2018/02/constraints-validation-for-user-inputs.html
        *
        * https://habr.com/ru/post/68318/
        */
        val validationFactory = Validation.buildDefaultValidatorFactory()
        val validator = validationFactory.validator
        val violations = validator.validate(userDTO)

        /*
        * violation.getPropertyPath().toString() - возвращает имя поля в котором возникла ошибка
        * violation.getMessage() - возвращает сообщение об ошибке
        * violation.getInvalidValue() - возвращает значение из-за которого возникла ошибка
        * */
        if (violations.isNotEmpty()) {

            val errors = HashMap<String, String>()
            for (violation in violations) {
                errors[violation.propertyPath.toString()] = violation.message
            }

            if (errors.containsKey("email")) {
                /*response.setError(107);
                response.setDescription(data.get("email").toString());
                return response;*/
                throw IncorrectEmailException()
            } else if (errors.containsKey("password")) {
                throw IncorrectPasswordException()
            } else if (errors.containsKey("phone")) {
                throw IncorrectPhoneException()
            }
        }

        //data.put("checkCode", controllerService.saveCheckCodeToEmail(userDTO.getEmail()));

        //controllerService.sendCheckCodeToEmail(userDTO.getEmail());

        response.data = controllerService!!.saveCheckCodeToEmail(userDTO.email, userDTO.isViaEmail)

        return response
    }

    @PostMapping("/reg-start")
    @Throws(IncorrectEmailException::class, LoginPasswordEqualException::class, IncorrectPasswordException::class, IncorrectPhoneException::class, DelayException::class)
    //(@Valid @RequestBody final UserDTO requestBody, BindingResult bindingResult)
    fun preRegistrationPostMethod(
            @Valid @RequestBody userDTO: UserDTO,
            bindingResult: BindingResult
    ): RestResponse {

        val response = RestResponse()

        //HashMap<String, Object> data = new HashMap<>();

        if (userDTO.password == userDTO.email) {
            throw LoginPasswordEqualException()
        }

        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldError

            if (errors!!.field == "email") {
                throw IncorrectEmailException()
            } else if (errors.field == "password") {
                throw IncorrectPasswordException()
            } else if (errors.field == "phone") {
                throw IncorrectPhoneException()
            }
        }

        //data.put("checkCode", controllerService.saveCheckCodeToEmail(userDTO.getEmail()));

        //controllerService.sendCheckCodeToEmail(userDTO.getEmail());

        response.data = controllerService!!.saveCheckCodeToEmail(userDTO.email, userDTO.isViaEmail)

        return response
    }

    /*
    *
    * Обработчики GET и POST запросов /reg-confirm
    *
    * используется для завершения регистрации с проверочным кодом из предыдущего метода
    * */


    @GetMapping("/reg-confirm")
    @Throws(IncorrectEmailException::class, LoginPasswordEqualException::class, IncorrectPasswordException::class, IncorrectPhoneException::class, DelayException::class, UserExistException::class)
    fun registrationConfirmGetMethod(
            @RequestParam email: String,
            @RequestParam password: String,
            @RequestParam phone: String,
            @RequestParam os: String,
            @RequestParam code: String,
            @RequestParam(required = false, defaultValue = "true") viaEmail: String,
            request: HttpServletRequest
    ): RestResponse {

        val response = RestResponse()

        //HashMap<String, Object> data = new HashMap<>();

        if (password == email) {
            throw LoginPasswordEqualException()
        }

        val userDTO = UserDTO()
        userDTO.email = email
        userDTO.password = password
        userDTO.phone = phone
        userDTO.os = os
        userDTO.isViaEmail = viaEmail == "true"

        /*
         * Про Validator взял отсюда
         * https://www.javaquery.com/2018/02/constraints-validation-for-user-inputs.html
         *
         * https://habr.com/ru/post/68318/
         */
        val validationFactory = Validation.buildDefaultValidatorFactory()
        val validator = validationFactory.validator
        val violations = validator.validate(userDTO)

        /*
         * violation.getPropertyPath().toString() - возвращает имя поля в котором возникла ошибка
         * violation.getMessage() - возвращает сообщение об ошибке
         * violation.getInvalidValue() - возвращает значение из-за которого возникла ошибка
         * */
        if (!violations.isEmpty()) {

            val errors = HashMap<String, String>()
            for (violation in violations) {
                errors[violation.propertyPath.toString()] = violation.message
            }

            if (errors.containsKey("email")) {
                throw IncorrectEmailException()
            } else if (errors.containsKey("password")) {
                throw IncorrectPasswordException()
            } else if (errors.containsKey("phone")) {
                throw IncorrectPhoneException()
            }
        }

        //data.put("checkCode", controllerService.saveCheckCodeToEmail(userDTO.getEmail()));

        //controllerService.sendCheckCodeToEmail(userDTO.getEmail());

        response.data = controllerService!!.checkOfVerificationCode(userDTO, code, request)

        return response
    }

    @PostMapping("/reg-confirm")
    @Throws(IncorrectEmailException::class, LoginPasswordEqualException::class, IncorrectPasswordException::class, IncorrectPhoneException::class, DelayException::class, UserExistException::class)
    //(@Valid @RequestBody final UserDTO requestBody, BindingResult bindingResult)
    fun registrationConfirmPostMethod(
            @Valid @RequestBody userDTO: UserDTO,
            bindingResult: BindingResult,
            request: HttpServletRequest
    ): RestResponse {

        val response = RestResponse()

        //HashMap<String, Object> data = new HashMap<>();

        if (userDTO.password == userDTO.email) {
            throw LoginPasswordEqualException()
        }

        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldError

            if (errors!!.field == "email") {
                throw IncorrectEmailException()
            } else if (errors.field == "password") {
                throw IncorrectPasswordException()
            } else if (errors.field == "phone") {
                throw IncorrectPhoneException()
            }
        }

        //data.put("checkCode", controllerService.saveCheckCodeToEmail(userDTO.getEmail()));

        //controllerService.sendCheckCodeToEmail(userDTO.getEmail());

        response.data = controllerService!!.checkOfVerificationCode(userDTO, userDTO.code, request)

        return response
    }

}
