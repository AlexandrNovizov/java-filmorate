package ru.yandex.practicum.filmorate.validate;

@FunctionalInterface
public interface Validate<T> {

    void validate(T object);
}
