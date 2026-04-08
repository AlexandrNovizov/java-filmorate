package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;

public class RatingDtoMapper {

    public static RatingDto mapToRatingDto(Rating entity) {
        RatingDto dto = new RatingDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());

        return dto;
    }

    public static Rating mapToRating(RatingDto dto) {
        Rating entity = new Rating();

        entity.setId(dto.getId());
        entity.setName(dto.getName());

        return entity;
    }
}
