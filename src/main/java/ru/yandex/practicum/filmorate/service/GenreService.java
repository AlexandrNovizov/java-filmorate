package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage storage;

    public Collection<GenreDto> getAll() {
        return storage.getAll();
    }

    public GenreDto getById(long genreId) {
        return storage.getById(genreId).orElseThrow(
                () -> new NotFoundException(String.format("Жанр с id %d не найден", genreId))
        );
    }
}
