package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getAll();

    Optional<Film> getById(Long id);

    Film create(Film object);

    Film update(Film object);

    void addLike(Film film, User user);

    void removeLike(Film film, User user);

    Long getNextId();
}
