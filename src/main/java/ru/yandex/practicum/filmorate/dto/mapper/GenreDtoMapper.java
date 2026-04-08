package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

public class GenreDtoMapper {

    public static GenreDto mapToGenreDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    public static Genre mapToGenre(GenreDto dto) {
        Genre.GenreBuilder builder = Genre.builder();
        builder.id(dto.getId());
        builder.name(dto.getName());
        return builder.build();
    }
}
