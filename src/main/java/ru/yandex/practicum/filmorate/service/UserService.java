package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validate.Validator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage storage;
    private final Validator<User> validator;

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
        user.setId(storage.getNextId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setFriends(new HashSet<>());
        validator.validate(user);
        return storage.create(user);
    }

    public User update(User user) {
        if (user.getId() == null) {
            log.warn("получен id == null");
            throw new ValidationException("id не может быть пустым");
        }

        User oldUser = getById(user.getId());
        User.UserBuilder builder = oldUser.toBuilder();
        setBuilderFields(builder, user);

        validator.validate(builder.build());
        User updatedUser = builder.build();
        return storage.update(updatedUser);
    }

    public void addToFriends(User user1, User user2) {
        storage.addToFriends(user1, user2);
    }

    public void removeFromFriends(User user1, User user2) {
        storage.removeFromFriends(user1, user2);
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
                .map(this::getById)
                .toList();
    }

    private void setBuilderFields(User.UserBuilder builder, User user) {
        if (user.getName() != null) {
            builder.name(user.getName());
        }

        if (user.getEmail() != null) {
            builder.email(user.getEmail());
        }

        if (user.getLogin() != null) {
            builder.login(user.getLogin());
        }

        if (user.getBirthday() != null) {
            builder.birthday(user.getBirthday());
        }
    }
}
