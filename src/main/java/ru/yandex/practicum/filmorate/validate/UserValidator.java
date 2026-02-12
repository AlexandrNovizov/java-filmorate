package ru.yandex.practicum.filmorate.validate;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class UserValidator implements Validate<User> {

    @Override
    public void validate(User user) {
        log.debug("валидация пользователя {}", user);
        if (user.getEmail() == null) {
            log.warn("email не может null");
            throw new ValidationException("электронная почта не может быть null");
        }
        if (user.getEmail().isBlank()) {
            log.warn("email не может быть пустым");
            throw new ValidationException("электронная почта не может быть пустой");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("email {} не прошел валидацию", user.getEmail());
            throw new ValidationException("электронная почта должна содержать символ @");
        }

        if (user.getLogin() == null) {
            log.warn("login не может null");
            throw new ValidationException("логин не может быть null");
        }

        if (user.getLogin().isBlank()) {
            log.warn("login не может быть пустым");
            throw new ValidationException("логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("login '{}' не прошел валидацию", user.getLogin());
            throw new ValidationException("логин не может содержать пробелы");
        }

        if (user.getBirthday() == null) {
            log.warn("день рождения не может быть null");
            throw new ValidationException("день рождения не может быть null");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("дата рождения {} не прошла валидацию", user.getBirthday());
            throw new ValidationException("день рождения не может быть в будущем");
        }

        log.info("пользователь {} прошел валидацию", user);
    }
}
