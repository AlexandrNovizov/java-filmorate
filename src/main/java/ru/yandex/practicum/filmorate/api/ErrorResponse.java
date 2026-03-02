package ru.yandex.practicum.filmorate.api;

import lombok.Data;

@Data
public class ErrorResponse {

    private final String error;
    private final String details;
}
