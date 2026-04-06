package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingService {

    private final RatingStorage ratingRepository;

    public Collection<RatingDto> getAll() {
        return ratingRepository.getAll();
    }

    public RatingDto getById(Long ratingId) {
        return ratingRepository.getById(ratingId).orElseThrow(
                () -> new NotFoundException(String.format("рейтинг с id '%d' не найден", ratingId))
        );
    }
}
