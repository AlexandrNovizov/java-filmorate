package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;
import ru.yandex.practicum.filmorate.validate.UserValidator;
import ru.yandex.practicum.filmorate.validate.Validator;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final InMemoryStorage<User> storage = new InMemoryStorage<>();
    private final Validator<User> validator = new UserValidator();

    @GetMapping
    public Collection<User> getAll() {
        return storage.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.debug("создание пользователя {}", user);
        validator.validate(user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(storage.getNextId());
        storage.put(user.getId(), user);
        log.debug("Добавлен пользователь {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.debug("обновление пользователя {}", newUser);
        if (newUser.getId() == null) {
            log.warn("получен id == null");
            throw new ValidationException("id не может быть пустым");
        }

        if (!storage.containsKey(newUser.getId())) {

            log.warn("пользователь с id {} не найден", newUser.getId());
            throw new NotFoundException(String.format("пользователь с id '%d' не найден", newUser.getId()));
        }

        User oldUser = storage.get(newUser.getId());

        User.UserBuilder builder = oldUser.toBuilder();

        setBuilderFields(builder, newUser);

        validator.validate(builder.build());
        oldUser = builder.build();
        log.debug("изменен пользователь {}", oldUser);
        storage.put(oldUser.getId(), oldUser);
        return oldUser;
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
