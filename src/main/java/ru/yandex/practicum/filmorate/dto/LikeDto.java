package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class LikeDto {

    private Long filmId;
    private Long userId;
}
