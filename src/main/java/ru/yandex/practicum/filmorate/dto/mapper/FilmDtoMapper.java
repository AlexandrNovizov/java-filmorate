package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

public class FilmDtoMapper {

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = FilmDto.builder().build();

        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());

        return dto;
    }

    public static Film mapToFilm(FilmDto dto) {
        Film.FilmBuilder builder = Film.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .duration(dto.getDuration())
                .releaseDate(dto.getReleaseDate())
                .ratingId(null);

        if (dto.getMpa() != null) {
            builder.ratingId(dto.getMpa().getId());
        }
        return builder.build();
    }
}
