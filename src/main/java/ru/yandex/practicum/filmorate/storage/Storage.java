package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface Storage<T, ID> {

    Collection<T> getAll();
    T getById(ID id);
    T create(T object);
    T update(T object);
}
