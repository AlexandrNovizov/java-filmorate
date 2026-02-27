package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> storage = new HashMap<>();

    private long nextId = -1;

    @Override
    public Collection<User> getAll() {
        return storage.values();
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public User create(User newUser) {
        storage.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User update(User updatedUser) {
        storage.put(updatedUser.getId(), updatedUser);
        return updatedUser;
    }

    @Override
    public void addToFriends(User user1, User user2) {
        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());
    }

    @Override
    public void removeFromFriends(User user1, User user2) {
        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());
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
