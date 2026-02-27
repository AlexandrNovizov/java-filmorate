package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getAll();

    Optional<User> getById(Long id);

    User create(User object);

    User update(User object);

    void addToFriends(User user1, User user2);

    void removeFromFriends(User user1, User user2);

    Long getNextId();
}
