package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.mapper.FilmDtoMapper;
import ru.yandex.practicum.filmorate.exception.InvalidParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validate.Validator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage storage;
    private final Validator<Film> validator;

    public FilmService(@Qualifier("filmDB") FilmStorage storage, Validator<Film> validator) {
        this.storage = storage;
        this.validator = validator;
    }

    public Collection<FilmDto> getAll() {
        return storage.getAll();
    }

    public Optional<FilmDto> getByIdOpt(Long filmId) {
        return storage.getById(filmId);
    }

    public FilmDto getById(Long filmId) {
        return getByIdOpt(filmId).orElseThrow(
                () -> new NotFoundException(String.format("фильм с id '%d' не найден", filmId))
        );
    }

    public FilmDto create(FilmDto film) {
        validator.validate(FilmDtoMapper.mapToFilm(film));
        FilmDto dto = storage.create(film);
        if (film.getGenres() == null) {
            dto.setGenres(new HashSet<>());
        }
        if (film.getLikes() == null) {
            dto.setLikes(new HashSet<>());
        }
        return dto;
    }

    public FilmDto update(Film film) {
        if (film.getId() == null) {
            log.warn("получен id == null");
            throw new ValidationException("id не может быть пустым");
        }

        FilmDto oldFilm = getById(film.getId());

        FilmDto.FilmDtoBuilder builder = oldFilm.toBuilder();

        setBuilderFields(builder, film);

        validator.validate(FilmDtoMapper.mapToFilm(builder.build()));
        FilmDto updatedFilm = builder.build();
        return storage.update(updatedFilm);
    }

    public void addLike(FilmDto film, User user) {
        storage.addLike(film, user);
    }

    public void removeLike(FilmDto film, User user) {
        storage.removeLike(film, user);
    }

    public Collection<FilmDto> getTopLikes(int size) {
        if (size <= 0) {
            throw new InvalidParameterException("Параметр count должен быть положительным");
        }

        return storage.getTopLikes(size);
    }

    private void setBuilderFields(FilmDto.FilmDtoBuilder builder, Film film) {
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
