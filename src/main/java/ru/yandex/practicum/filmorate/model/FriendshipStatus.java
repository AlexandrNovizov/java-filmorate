package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FriendshipStatus {
    private long id;
    private FriendshipStatusEnum status;
}
