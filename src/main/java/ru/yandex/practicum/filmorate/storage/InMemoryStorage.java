package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
public class InMemoryStorage<T> extends HashMap<Long, T> {

    private long nextId = -1;

    public Long getNextId() {
        log.trace("выполняется генерация id");
        if (nextId < 0) {
            nextId = keySet().stream()
                    .mapToLong(id -> id)
                    .max()
                    .orElse(0);
        }

        log.debug("сгенерирован id {}", nextId + 1);
        return ++nextId;
    }
}
