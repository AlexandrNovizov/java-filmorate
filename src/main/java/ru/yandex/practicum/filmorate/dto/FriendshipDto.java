package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

@Data
public class FriendshipDto {

    private Long userId;
    private Long friendId;
    private FriendshipStatus status;
}
