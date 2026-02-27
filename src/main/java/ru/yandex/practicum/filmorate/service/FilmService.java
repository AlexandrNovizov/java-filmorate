package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InvalidParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validate.Validator;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage storage;
    private final Validator<Film> validator;

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
        film.setId(storage.getNextId());
        film.setLikes(new HashSet<>());
        validator.validate(film);
        return storage.create(film);
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            log.warn("получен id == null");
            throw new ValidationException("id не может быть пустым");
        }

        Film oldFilm = getById(film.getId());

        Film.FilmBuilder builder = oldFilm.toBuilder();

        setBuilderFields(builder, film);

        validator.validate(builder.build());
        Film updatedFilm = builder.build();
        return storage.update(updatedFilm);
    }

    public void addLike(Film film, User user) {
        storage.addLike(film, user);
    }

    public void removeLike(Film film, User user) {
        storage.removeLike(film, user);
    }

    public Collection<Film> getTopLikes(int size) {
        if (size <= 0) {
            throw new InvalidParameterException("Параметр count должен быть положительным");
        }

        Comparator<Object> comparator = Comparator
                .comparingInt(film -> ((Film) film).getLikes().size())
                .reversed();

        return storage.getAll().stream()
                .sorted(comparator)
                .limit(size)
                .toList();
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
