package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage storage;

    public Collection<Film> getAll() {
        return storage.getAll();
    }

    public Optional<Film> getByIdOpt(Long filmId) {
        return storage.getById(filmId);
    }

    public Film getById(Long filmId) {
        return getByIdOpt(filmId).orElseThrow(
                () -> new NotFoundException(String.format("фильм с id '%d' не найден", filmId))
        );
    }

    public Film create(Film film) {
        return storage.create(film);
    }

    public Film update(Film film) {
        return storage.update(film);
    }

    public void addLike(Film film, User user) {
        film.getLikes().add(user.getId());
    }

    public void removeLike(Film film, User user) {
        film.getLikes().remove(user.getId());
    }

    public Collection<Film> getTopLikes(int size) {

        Comparator<Object> comparator = Comparator
                .comparingInt(film -> ((Film) film).getLikes().size())
                .reversed();

        return storage.getAll().stream()
                .sorted(comparator)
                .limit(size)
                .toList();
    }
}
