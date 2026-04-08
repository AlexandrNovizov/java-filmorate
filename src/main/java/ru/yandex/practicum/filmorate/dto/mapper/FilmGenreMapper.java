package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.FilmGenreDto;
import ru.yandex.practicum.filmorate.model.FilmGenre;

public class FilmGenreMapper {

    public static FilmGenreDto mapToFilmGenreDto(FilmGenre entity) {
        FilmGenreDto dto = new FilmGenreDto();

        dto.setGenreId(entity.getGenreId());
        dto.setFilmId(entity.getFilmId());
        dto.setGenreName(entity.getGenreName());

        return dto;
    }

    public static FilmGenre mapToFilmGenre(FilmGenreDto dto) {
        FilmGenre entity = new FilmGenre();

        entity.setGenreId(dto.getGenreId());
        entity.setFilmId(dto.getFilmId());
        entity.setGenreName(dto.getGenreName());

        return entity;
    }
}
