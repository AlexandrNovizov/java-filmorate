package ru.yandex.practicum.filmorate.validate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    private static final Validate<User> validator = new UserValidator();
    private static User.UserBuilder correctUserBuilder;

    @BeforeEach
    void init() {
        correctUserBuilder = User.builder()
                .email("user@gmail.com")
                .login("login")
                .birthday(LocalDate.now().minusYears(10));
    }

    @Test
    void shouldThrowValidationExceptionIfEmailIsNull() {
        User user = correctUserBuilder
                .email(null)
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(user));

        final String message = "электронная почта не может быть null";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionIfLoginIsNull() {
        User user = correctUserBuilder
                .login(null)
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(user));

        final String message = "логин не может быть null";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionIfEmailIsBlank() {
        User user = correctUserBuilder
                .email("  ")
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(user));

        final String message = "электронная почта не может быть пустой";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionIfEmailNotContainsAtSymbol() {
        User user = correctUserBuilder
                .email("user.gmail.com")
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(user));

        final String message = "электронная почта должна содержать символ @";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionIfLoginIsBlank() {
        User user = correctUserBuilder
                .login("  ")
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(user));

        final String message = "логин не может быть пустым";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionIfLoginContainsSpace() {
        User user = correctUserBuilder
                .login("my perfect login")
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(user));

        final String message = "логин не может содержать пробелы";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionIfBirthdayIsNull() {
        User user = correctUserBuilder
                .birthday(null)
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(user));

        final String message = "день рождения не может быть null";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionIfBirthdayIsInFuture() {
        User user = correctUserBuilder
                .birthday(LocalDate.now().plusDays(1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(user));

        final String message = "день рождения не может быть в будущем";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldPassIfUserIsCorrect() {
        User user = correctUserBuilder
                .build();

        assertDoesNotThrow(() -> validator.validate(user));
    }
}