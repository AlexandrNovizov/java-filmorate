package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.mapper.FilmDtoMapper;
import ru.yandex.practicum.filmorate.exception.InvalidParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.validate.Validator;

import java.util.*;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final Validator<Film> validator;

    public FilmService(FilmStorage filmStorage, Validator<Film> validator, GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.validator = validator;
        this.genreStorage = genreStorage;
    }

    public Collection<FilmDto> getAll() {
        return filmStorage.getAll();
    }

    public Optional<FilmDto> getByIdOpt(Long filmId) {
        return filmStorage.getById(filmId);
    }

    public FilmDto getById(Long filmId) {
        return getByIdOpt(filmId).orElseThrow(
                () -> new NotFoundException(String.format("фильм с id '%d' не найден", filmId))
        );
    }

    public FilmDto create(FilmDto film) {
        validator.validate(FilmDtoMapper.mapToFilm(film));
        FilmDto dto = filmStorage.create(film);
        if (film.getGenres() == null) {
            dto.setGenres(new HashSet<>());
        } else if (!film.getGenres().isEmpty()) {
            List<Long> genreIds = film.getGenres().stream().map(GenreDto::getId).toList();

            Optional<Long> missedId = genreStorage.checkGenreIds(genreIds);
            if (missedId.isPresent()) {
                throw new NotFoundException(String.format("Жанр с id '%d' не найден", missedId.get()));
            }

            Collection<GenreDto> genreDtos = genreStorage.getAllFromCollection(genreIds);

            genreStorage.createForFilm(film.getId(), genreIds);
            film.setGenres(genreDtos);
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
        return filmStorage.update(updatedFilm);
    }

    public void addLike(FilmDto film, User user) {
        filmStorage.addLike(film, user);
    }

    public void removeLike(FilmDto film, User user) {
        filmStorage.removeLike(film, user);
    }

    public Collection<FilmDto> getTopLiked(int size) {
        if (size <= 0) {
            throw new InvalidParameterException("Параметр count должен быть положительным");
        }

        Collection<FilmDto> topLiked = filmStorage.getTopLiked(size);

        Map<Long, List<GenreDto>> genresMap = genreStorage.allGenresByFilms();

        for (FilmDto filmDto : topLiked) {

            List<GenreDto> filmGenres = genresMap.getOrDefault(filmDto.getId(), new ArrayList<>());

            Comparator<GenreDto> genreDtoComparator = Comparator.comparingLong(GenreDto::getId);
            filmGenres.sort(genreDtoComparator);
            filmDto.setGenres(filmGenres);
        }

        return topLiked;
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
