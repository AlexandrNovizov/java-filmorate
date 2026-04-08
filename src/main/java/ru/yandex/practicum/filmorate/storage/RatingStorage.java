package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.RatingDto;

import java.util.Collection;
import java.util.Optional;

public interface RatingStorage {

    Collection<RatingDto> getAll();

    Optional<RatingDto> getById(Long ratingId);
}
