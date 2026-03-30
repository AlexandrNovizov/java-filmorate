package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class UserService {

    private final UserStorage storage;
    private final Validator<User> validator;

    public UserService(@Qualifier("userDB") UserStorage storage, Validator<User> validator) {
        this.storage = storage;
        this.validator = validator;
    }

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
        return storage.getFriends(user);
    }

    public Collection<User> getCommonFriends(User user1, User user2) {

        return storage.getCommonFriends(user1, user2);
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
