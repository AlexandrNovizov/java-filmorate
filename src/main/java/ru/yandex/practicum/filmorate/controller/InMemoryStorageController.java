package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class InMemoryStorageController<T> {

    protected final Map<Long, T> storage = new HashMap<>();

    protected Long getNextId() {
        log.trace("выполняется генерация id");
        long maxId = storage.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.debug("сгенерирован id {}", maxId + 1);
        return ++maxId;
    }
}
