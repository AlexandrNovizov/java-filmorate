package ru.yandex.practicum.filmorate.validate;

@FunctionalInterface
public interface Validator<T> {

    void validate(T object);
}
