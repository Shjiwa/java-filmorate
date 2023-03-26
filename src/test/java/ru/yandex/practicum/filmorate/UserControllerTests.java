package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTests extends UserController {

    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }


    @Test
    void shouldNotValidateNullUser() {
        User user = new User();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(2, violations.size(), "User is valid.");
    }

    @Test
    void shouldValidateCorrectUser() {
        User user = new User();
        user.setEmail("correct@email.com");
        user.setName("Anton");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setLogin("Anton2000");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size(), "User is not valid.");
    }

    @Test
    void shouldNotValidateNullEmail() {
        User user = new User();
        user.setEmail(null);
        user.setName("Anton");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setLogin("Anton2000");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Email is not null or blank.");
    }

    @Test
    void shouldNotValidateBlankEmail() {
        User user = new User();
        user.setEmail("");
        user.setName("Anton");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setLogin("Anton2000");


        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Email is valid.");
    }

    @Test
    void shouldNotValidateEmailWithWhitespaces() {
        User user = new User();
        user.setEmail("te st @mail.box");
        user.setName("Anton");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setLogin("Anton2000");


        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Email without whitespaces.");
    }

    @Test
    void shouldNotValidateEmailWithoutAtSymbol() {
        User user = new User();
        user.setEmail("testmail.box");
        user.setName("Anton");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setLogin("Anton2000");


        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Email contains: @");
    }

    @Test
    void shouldNotValidateNullLogin() {
        User user = new User();
        user.setEmail("test@mail.box");
        user.setName("Anton");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setLogin(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Login is not null.");
    }

    @Test
    void shouldNotValidateBlankLogin() {
        User user = new User();
        user.setEmail("test@mail.box");
        user.setName("Anton");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Login is valid.");
    }

    @Test
    void shouldNotValidateLoginWithWhitespaces() {
        User user = new User();
        user.setEmail("test@mail.box");
        user.setName("Anton");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setLogin("Anton 2000");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Login without whitespaces.");
    }

    @Test
    void shouldValidateNullName() {
        User user = new User();
        user.setEmail("test@mail.box");
        user.setName(null);
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setLogin("Anton2000");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size(), "Can't validate user name.");
    }

    @Test
    void shouldValidateBlankName() {
        User user = new User();
        user.setEmail("test@mail.box");
        user.setLogin("Anton2000");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size(), "Can't validate user name.");
    }

    @Test
    void shouldNotValidateBirthdayInFuture() {
        User user = new User();
        user.setEmail("test@mail.box");
        user.setName("Anton");
        user.setBirthday(LocalDate.of(2222, 1, 1));
        user.setLogin("Anton2000");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Birthday in past.");
    }
}