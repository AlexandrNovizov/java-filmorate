package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final UserService userService;

    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable Long id) {
        return filmService.getById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getMostPopular(@RequestParam(value = "count", defaultValue = "10") Integer count) {
        if (count <= 0) {
            throw new InvalidParameterException("Параметр count должен быть положительным");
        }

        log.debug("получение {} самых популярных фильмов", count);
        return filmService.getTopLikes(count);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.debug("создание фильма {}", film);
        Film createdFilm = filmService.create(film);
        log.info("добавлен фильм {}", createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.debug("обновление фильма {}", newFilm);
        Film updatedFilm = filmService.update(newFilm);
        log.info("обновлен фильм {}", updatedFilm);
        return updatedFilm;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable Long filmId,
                        @PathVariable Long userId) {


        Film film = filmService.getById(filmId);
        User user = userService.getById(userId);

        filmService.addLike(film, user);
        log.debug("пользователь {} поставил лайк фильму {}", user, film);
        return film;
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable Long filmId,
                           @PathVariable Long userId) {

        Film film = filmService.getById(filmId);
        User user = userService.getById(userId);

        filmService.removeLike(film, user);
        log.debug("пользователь {} удалил лайк фильму {}", user, film);
        return film;
    }
}
