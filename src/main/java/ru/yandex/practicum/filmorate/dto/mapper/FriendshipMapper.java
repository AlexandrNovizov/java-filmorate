package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.FriendshipDto;
import ru.yandex.practicum.filmorate.model.Friendship;

public class FriendshipMapper {

    public static FriendshipDto mapToFriendshipDto(Friendship entity) {
        FriendshipDto dto = new FriendshipDto();

        dto.setUserId(entity.getUserId());
        dto.setFriendId(entity.getFriendId());
        dto.setStatus(entity.getStatus());

        return dto;
    }

    public static Friendship mapToFriendship(FriendshipDto dto) {
        Friendship entity = new Friendship();

        entity.setUserId(dto.getUserId());
        entity.setFriendId(dto.getFriendId());
        entity.setStatus(dto.getStatus());

        return entity;
    }
}
