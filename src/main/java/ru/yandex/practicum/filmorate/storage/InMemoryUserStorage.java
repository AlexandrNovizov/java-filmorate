package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validate.Validator;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> storage = new HashMap<>();
    private final Validator<User> validator;

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
        newUser.setId(getNextId());
        if (newUser.getName() == null) {
            newUser.setName(newUser.getLogin());
        }
        newUser.setFriends(new HashSet<>());
        validator.validate(newUser);
        storage.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User update(User user) {
        if (user.getId() == null) {
            log.warn("получен id == null");
            throw new ValidationException("id не может быть пустым");
        }
        if (!storage.containsKey(user.getId())) {
            log.warn("пользователь с id {} не найден", user.getId());
            throw new NotFoundException(String.format("пользователь с id '%d' не найден", user.getId()));
        }

        User oldUser = storage.get(user.getId());
        User.UserBuilder builder = oldUser.toBuilder();
        setBuilderFields(builder, user);

        validator.validate(builder.build());
        User updatedUser = builder.build();
        storage.put(updatedUser.getId(), updatedUser);

        return updatedUser;
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
