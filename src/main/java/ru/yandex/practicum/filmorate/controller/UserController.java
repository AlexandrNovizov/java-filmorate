package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public Collection<User> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.debug("создание пользователя {}", user);
        user = service.create(user);
        log.debug("Добавлен пользователь {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.debug("обновление пользователя {}", newUser);
        User updatedUser = service.update(newUser);
        log.debug("обновлен пользователь {}", updatedUser);
        return updatedUser;
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {

        User user = service.getById(id);
        log.debug("получаем друзей {}", user);
        return service.getFriends(user);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long userId,
                                             @PathVariable Long otherId) {

        User user = service.getById(userId);
        User other = service.getById(otherId);

        log.debug("получаем общих друзей {} и {}", user, other);
        return service.getCommonFriends(user, other);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Long userId,
                          @PathVariable Long friendId) {

        User user = service.getById(userId);
        User friend = service.getById(friendId);

        log.debug("добавляем {} в друзья {}", friend, user);
        service.addToFriends(user, friend);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long userId,
                             @PathVariable Long friendId) {

        User user = service.getById(userId);
        User friend = service.getById(friendId);

        log.debug("удаляем {} из друзей {}", friend, user);
        service.removeFromFriends(user, friend);
    }
}
