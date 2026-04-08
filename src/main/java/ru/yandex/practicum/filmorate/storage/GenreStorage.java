package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.FilmGenreDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GenreStorage {

    Collection<GenreDto> getAll();

    Optional<GenreDto> getById(long genreId);

    Collection<GenreDto> getAllFromCollection(Collection<Long> genreIds);

    Optional<Long> checkGenreIds(Collection<Long> genreIds);

    Map<Long, List<GenreDto>> allGenresByFilms();

    void createForFilm(Long filmId, Collection<Long> genreIds);
}
