package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

@Data
public class FriendshipStatusDto {
    private long id;
    private FriendshipStatus status;
}
