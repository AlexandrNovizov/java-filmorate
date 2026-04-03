package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate.storage")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DbFilmStorageTest {

    private final DbFilmStorage filmStorage;
    private final DbUserStorage userStorage;

    @Test
    void testCreateUserShouldAddUser() {
        Film film = Film.builder()
                .name("test")
                .releaseDate(LocalDate.now())
                .description("desc")
                .build();

        Collection<Film> films = filmStorage.getAll();
        assertEquals(0, films.size());

        filmStorage.create(film);

        films = filmStorage.getAll();
        assertEquals(1, films.size());
    }

    @Test
    void testFindFilmByIdShouldReturnFilm() {
        Film film = Film.builder()
                .name("test")
                .releaseDate(LocalDate.now())
                .description("desc")
                .build();

        filmStorage.create(film);

        long expectedId = 1L;

        Optional<Film> filmOptional = filmStorage.getById(expectedId);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(receivedFilm -> assertThat(receivedFilm)
                                .hasFieldOrPropertyWithValue("id", expectedId)
                                .hasFieldOrPropertyWithValue("name", "test")
                );
    }

    @Test
    void testGetAllShouldReturnAllFilms() {
        Film film1 = Film.builder()
                .name("test")
                .releaseDate(LocalDate.now())
                .description("desc")
                .build();

        Film film2 = Film.builder()
                .name("test2")
                .releaseDate(LocalDate.now())
                .description("desc2")
                .build();

        filmStorage.create(film1);
        filmStorage.create(film2);

        Collection<Film> films = filmStorage.getAll();

        assertThat(films)
                .matches(allUsers -> allUsers.size() == 2)
                .matches(allUsers -> allUsers.contains(film1))
                .matches(allUsers -> allUsers.contains(film2));
    }

    @Test
    void testUpdateFilmShouldChangeFilm() {
        String initialName = "test";
        String updatedName = "updated test";

        Film createdFilm = Film.builder()
                .name(initialName)
                .releaseDate(LocalDate.now())
                .description("desc")
                .build();

        filmStorage.create(createdFilm);
        createdFilm.setName(updatedName);
        Film updatedFilm = filmStorage.update(createdFilm);

        assertThat(updatedFilm)
                .matches(film -> film.getName().equals(updatedName));
    }

    @Test
    void testAddLikeShouldAddUserIdToLikes() {
        User user = User.builder()
                .email("testuser@mail.ru")
                .login("testlogin")
                .name("testname")
                .birthday(LocalDate.now())
                .build();

        Film film = Film.builder()
                .name("test")
                .releaseDate(LocalDate.now())
                .description("desc")
                .build();

        filmStorage.create(film);
        userStorage.create(user);

        filmStorage.addLike(film, user);

        assertTrue(film.getLikes().contains(user.getId()));
    }

    @Test
    void testRemoveLikeShouldRemoveUserIdFromLikes() {
        User user = User.builder()
                .email("testuser@mail.ru")
                .login("testlogin")
                .name("testname")
                .birthday(LocalDate.now())
                .build();

        Film film = Film.builder()
                .name("test")
                .releaseDate(LocalDate.now())
                .description("desc")
                .build();

        filmStorage.create(film);
        userStorage.create(user);

        filmStorage.addLike(film, user);

        assertTrue(film.getLikes().contains(user.getId()));

        filmStorage.removeLike(film, user);

        assertThat(film.getLikes())
                .matches(Collection::isEmpty);
    }
}