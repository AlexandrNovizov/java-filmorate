package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final InMemoryUserStorage storage;

    @GetMapping
    public Collection<User> getAll() {
        return storage.getAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.debug("создание пользователя {}", user);
        user = storage.create(user);
        log.debug("Добавлен пользователь {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.debug("обновление пользователя {}", newUser);
        User updatedUser = storage.update(newUser);
        log.debug("обновлен пользователь {}", updatedUser);
        return updatedUser;
    }
}
