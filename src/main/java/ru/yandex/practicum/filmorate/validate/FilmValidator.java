package ru.yandex.practicum.filmorate.validate;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.*;

@Slf4j
public class FilmValidator implements Validate<Film> {

    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate FIRST_FILM_RELEASE_DATE =
            LocalDate.of(1895, Month.DECEMBER, 28);

    @Override
    public void validate(Film film) {
        log.debug("валидация фильма {}", film);
        if (film.getName() == null) {
            log.warn("имя не может быть null");
            throw new ValidationException("имя не может быть null");
        }

        if (film.getName().isBlank()) {
            log.warn("название {} не прошло валидацию", film.getName());
            throw new ValidationException("название не может быть пустым");
        }

        if (film.getDescription() == null) {
            log.warn("описание не может быть null");
            throw new ValidationException("описание не может быть null");
        }

        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.warn("описание {} не прошло валидацию", film.getDescription());
            throw new ValidationException("описание не может быть длиннее 200 символов");
        }

        if (film.getReleaseDate() == null) {
            log.warn("дата выхода не может быть null");
            throw new ValidationException("дата выхода не может быть null");
        }

        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE_DATE)) {
            log.warn("дата выхода {} не прошла валидацию", film.getReleaseDate());
            throw new ValidationException("дата релиза не может быть раньше 28.12.1895");
        }

        if (film.getDuration() <= 0) {
            log.warn("продолжительность {} не прошло валидацию", film.getDescription());
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        }
        log.info("фильм {} прошел валидацию", film);
    }
}
