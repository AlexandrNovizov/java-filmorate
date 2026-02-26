package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validate.Validator;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> storage = new HashMap<>();
    private final Validator<Film> validator;

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
        newFilm.setId(getNextId());
        newFilm.setLikes(new HashSet<>());
        validator.validate(newFilm);
        storage.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("получен id == null");
            throw new ValidationException("id не может быть пустым");
        }

        if (!storage.containsKey(newFilm.getId())) {
            log.warn("фильм с id {} не найден", newFilm.getId());
            throw new NotFoundException(String.format("фильм с id '%d' не найден", newFilm.getId()));
        }

        Film oldFilm = storage.get(newFilm.getId());

        Film.FilmBuilder builder = oldFilm.toBuilder();

        setBuilderFields(builder, newFilm);

        validator.validate(builder.build());
        Film updatedFilm = builder.build();
        storage.put(updatedFilm.getId(), updatedFilm);
        return updatedFilm;
    }

    private void setBuilderFields(Film.FilmBuilder builder, Film film) {
        if (film.getName() != null) {
            builder.name(film.getName());
        }

        if (film.getDescription() != null) {
            builder.description(film.getDescription());
        }

        if (film.getReleaseDate() != null) {
            builder.releaseDate(film.getReleaseDate());
        }

        if (film.getDuration() != 0) {
            builder.duration(film.getDuration());
        }
    }

    private Long getNextId() {
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
