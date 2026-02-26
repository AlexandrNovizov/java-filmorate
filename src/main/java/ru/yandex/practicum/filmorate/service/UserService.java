package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
// TODO: null references check (special exception?)
public class UserService {

    private final UserStorage storage;

    public Collection<User> getAll() {
        return storage.getAll();
    }

    public Optional<User> getByIdOpt(Long userId) {
        return storage.getById(userId);
    }

    public User getById(Long userId) {
        return getByIdOpt(userId).orElseThrow(
                () -> new NotFoundException(String.format("пользователь с id '%d' не найден", userId))
        );
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(User user) {
        return storage.update(user);
    }

    public void addToFriends(User user1, User user2) {
        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());
    }

    public void removeFromFriends(User user1, User user2) {
        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());
    }

    public Collection<User> getFriends(User user) {
        return user.getFriends().stream()
                .map(this::getById)
                .toList();
    }

    public Collection<User> getCommonFriends(User user1, User user2) {

        Set<Long> intersection = new HashSet<>(user1.getFriends());
        intersection.retainAll(user2.getFriends());
        return intersection.stream()
                .map(id -> {
                    Optional<User> optUser = storage.getById(id);
                    return optUser.orElseThrow(
                            () -> new NotFoundException(String.format("пользователь с id '%d' не найден", id))
                    );
                })
                .toList();
    }
}
