package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<FilmDto> getAll();

    Optional<FilmDto> getById(Long id);

    FilmDto create(FilmDto object);

    FilmDto update(FilmDto object);

    void addLike(FilmDto film, User user);

    void removeLike(FilmDto film, User user);

    Collection<FilmDto> getTopLikes(int size);
}
