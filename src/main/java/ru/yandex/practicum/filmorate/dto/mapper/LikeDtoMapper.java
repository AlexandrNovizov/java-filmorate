package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.LikeDto;
import ru.yandex.practicum.filmorate.model.Like;

public class LikeDtoMapper {

    public static LikeDto mapToLikeDto(Like entity) {
        LikeDto dto = new LikeDto();

        dto.setUserId(entity.getUserId());
        dto.setFilmId(entity.getFilmId());

        return dto;
    }

    public static Like mapToLike(LikeDto dto) {
        Like entity = new Like();

        entity.setUserId(dto.getUserId());
        entity.setFilmId(dto.getFilmId());

        return entity;
    }
}
