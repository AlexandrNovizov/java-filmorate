package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final InMemoryFilmStorage storage;

    @GetMapping
    public Collection<Film> getAll() {
        return storage.getAll();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.debug("создание фильма {}", film);
        Film createdFilm = storage.create(film);
        log.info("добавлен фильм {}", createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.debug("обновление фильма {}", newFilm);
        Film updatedFilm = storage.update(newFilm);
        log.info("обновлен фильм {}", updatedFilm);
        return updatedFilm;
    }
}
