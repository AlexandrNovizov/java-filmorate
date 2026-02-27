package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> storage = new HashMap<>();

    private long nextId = -1;

    @Override
    public Collection<Film> getAll() {
        return storage.values();
    }

    @Override
    public Optional<Film> getById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Film create(Film newFilm) {
        storage.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film update(Film updatedFilm) {
        storage.put(updatedFilm.getId(), updatedFilm);
        return updatedFilm;
    }

    @Override
    public void addLike(Film film, User user) {
        film.getLikes().add(user.getId());
    }

    @Override
    public void removeLike(Film film, User user) {
        film.getLikes().remove(user.getId());
    }

    public Long getNextId() {
        log.trace("выполняется генерация id");
        if (nextId < 0) {
            nextId = storage.keySet().stream()
                    .mapToLong(id -> id)
                    .max()
                    .orElse(0);
        }

        log.debug("сгенерирован id {}", nextId + 1);
        return ++nextId;
    }
}
