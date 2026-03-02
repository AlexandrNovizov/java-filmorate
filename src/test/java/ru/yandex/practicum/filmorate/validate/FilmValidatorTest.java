package ru.yandex.practicum.filmorate.validate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {

    private static final Validator<Film> validator = new FilmValidator();
    private static Film.FilmBuilder correctFilmBuilder = Film.builder()
            .name("name")
            .description("description")
            .releaseDate(LocalDate.now())
            .duration(120);

    @BeforeEach
    void init() {
        correctFilmBuilder = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(120);
    }

    @Test
    void shouldThrowValidationExceptionIfNameIsNull() {
        Film film = correctFilmBuilder
                .name(null)
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(film));

        String message = "имя не может быть null";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionIfNameIsBlank() {
        Film film = correctFilmBuilder
                .name("  ")
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(film));

        String message = "название не может быть пустым";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionIfDescriptionIsNull() {
        Film film = correctFilmBuilder
                .description(null)
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(film));

        String message = "описание не может быть null";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionIfDescriptionIsBiggerThan200() {
        Film film = correctFilmBuilder
                .description("a".repeat(201))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(film));

        String message = "описание не может быть длиннее 200 символов";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionIfReleaseDateIsNull() {
        Film film = correctFilmBuilder
                .releaseDate(null)
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(film));

        String message = "дата выхода не может быть null";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionIfReleaseDateIsBeforeFirstFilmRelease() {
        Film film = correctFilmBuilder
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 28).minusDays(1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(film));

        String message = "дата релиза не может быть раньше 28.12.1895";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionIfDurationIsEqualZero() {
        Film film = correctFilmBuilder
                .duration(0)
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(film));

        String message = "продолжительность фильма должна быть положительным числом";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionIfDurationIsLessThenZero() {
        Film film = correctFilmBuilder
                .duration(-1)
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> validator.validate(film));

        String message = "продолжительность фильма должна быть положительным числом";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldPassIfFilmIsCorrect() {
        Film film = correctFilmBuilder.build();

        assertDoesNotThrow(() -> validator.validate(film));
    }
}