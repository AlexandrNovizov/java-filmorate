package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.FriendshipStatusDto;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

public class FriendshipStatusDtoMapper {

    public static FriendshipStatusDto mapToFriendshipStatusDto(FriendshipStatus entity) {
        FriendshipStatusDto dto = new FriendshipStatusDto();

        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());

        return dto;
    }

    public static FriendshipStatus mapToFriendshipStatus(FriendshipStatusDto dto) {
        FriendshipStatus entity = new FriendshipStatus();

        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());

        return entity;
    }
}
