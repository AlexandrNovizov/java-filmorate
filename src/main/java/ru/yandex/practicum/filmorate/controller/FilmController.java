package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;
import ru.yandex.practicum.filmorate.validate.FilmValidator;
import ru.yandex.practicum.filmorate.validate.Validator;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final InMemoryStorage<Film> storage = new InMemoryStorage<>();
    private final Validator<Film> validator = new FilmValidator();

    @GetMapping
    public Collection<Film> getAll() {
        return storage.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.debug("создание фильма {}", film);
        validator.validate(film);
        film.setId(storage.getNextId());
        storage.put(film.getId(), film);
        log.info("добавлен фильм {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.debug("обновление фильма {}", newFilm);
        if (newFilm.getId() == null) {
            log.warn("получен id == null");
            throw new ValidationException("id не может быть пустым");
        }

        if (!storage.containsKey(newFilm.getId())) {
            log.warn("фильм с id {} не найден", newFilm.getId());
            throw new NotFoundException(String.format("фильм с id '%d' не найден", newFilm.getId()));
        }

        Film oldFilm = storage.get(newFilm.getId());

        Film.FilmBuilder builder = oldFilm.toBuilder();

        setBuilderFields(builder, newFilm);

        validator.validate(builder.build());
        oldFilm = builder.build();
        log.info("обновлен фильм {}", oldFilm);
        return oldFilm;
    }

    private void setBuilderFields(Film.FilmBuilder builder, Film film) {
        if (film.getName() != null) {
            builder.name(film.getName());
        }

        if (film.getDescription() != null) {
            builder.description(film.getDescription());
        }

        if (film.getReleaseDate() != null) {
            builder.releaseDate(film.getReleaseDate());
        }

        if (film.getDuration() != 0) {
            builder.duration(film.getDuration());
        }
    }
}
