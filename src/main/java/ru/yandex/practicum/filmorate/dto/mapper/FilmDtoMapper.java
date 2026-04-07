package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.model.Film;

public class FilmDtoMapper {

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = FilmDto.builder().build();

        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());

        if (film.getRatingId() == null && film.getRatingName() == null) {
            dto.setMpa(null);
        } else {
            RatingDto ratingDto = new RatingDto();
            ratingDto.setId(film.getRatingId());
            ratingDto.setName(film.getRatingName());
            dto.setMpa(ratingDto);
        }
        return dto;
    }

    public static Film mapToFilm(FilmDto dto) {
        Film.FilmBuilder builder = Film.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .duration(dto.getDuration())
                .releaseDate(dto.getReleaseDate())
                .ratingId(null)
                .ratingName(null);

        if (dto.getMpa() != null) {
            builder.ratingId(dto.getMpa().getId());
            builder.ratingName(dto.getMpa().getName());
        }
        return builder.build();
    }
}
