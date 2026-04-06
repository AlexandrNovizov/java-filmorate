package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dto.FilmDto;
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
        FilmDto film = FilmDto.builder()
                .name("test")
                .releaseDate(LocalDate.now())
                .description("desc")
                .build();

        Collection<FilmDto> films = filmStorage.getAll();
        assertEquals(0, films.size());

        filmStorage.create(film);

        films = filmStorage.getAll();
        assertEquals(1, films.size());
    }

    @Test
    void testFindFilmByIdShouldReturnFilm() {
        FilmDto film = FilmDto.builder()
                .name("test")
                .releaseDate(LocalDate.now())
                .description("desc")
                .build();

        filmStorage.create(film);

        long expectedId = 1L;

        Optional<FilmDto> filmOptional = filmStorage.getById(expectedId);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(receivedFilm -> assertThat(receivedFilm)
                                .hasFieldOrPropertyWithValue("id", expectedId)
                                .hasFieldOrPropertyWithValue("name", "test")
                );
    }

    @Test
    void testGetAllShouldReturnAllFilms() {
        FilmDto expected1 = FilmDto.builder()
                .id(1L)
                .name("test")
                .releaseDate(LocalDate.now())
                .description("desc")
                .build();

        FilmDto expected2 = FilmDto.builder()
                .id(2L)
                .name("test2")
                .releaseDate(LocalDate.now())
                .description("desc2")
                .build();

        filmStorage.create(expected1);
        filmStorage.create(expected2);

        Collection<FilmDto> films = filmStorage.getAll();

        assertThat(films)
                .matches(allUsers -> allUsers.size() == 2)
                .matches(allUsers -> allUsers.contains(expected1))
                .matches(allUsers -> allUsers.contains(expected2));
    }

    @Test
    void testUpdateFilmShouldChangeFilm() {
        String initialName = "test";
        String updatedName = "updated test";

        FilmDto createdFilm = FilmDto.builder()
                .name(initialName)
                .releaseDate(LocalDate.now())
                .description("desc")
                .build();

        createdFilm = filmStorage.create(createdFilm);
        createdFilm.setName(updatedName);
        FilmDto updatedFilm = filmStorage.update(createdFilm);

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

        FilmDto film = FilmDto.builder()
                .name("test")
                .releaseDate(LocalDate.now())
                .description("desc")
                .build();

        film = filmStorage.create(film);
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

        FilmDto film = FilmDto.builder()
                .name("test")
                .releaseDate(LocalDate.now())
                .description("desc")
                .build();

        film = filmStorage.create(film);
        userStorage.create(user);

        filmStorage.addLike(film, user);

        assertTrue(film.getLikes().contains(user.getId()));

        filmStorage.removeLike(film, user);

        assertThat(film.getLikes())
                .matches(Collection::isEmpty);
    }

    @Test
    void testGetTopLikesShouldReturnMostLikedFilms() {
        int filmsCount = 5;
        int usersCount = 3;
        int topLikesSize = 3;
        for (int i = 0; i < filmsCount; i++) {
            FilmDto film = FilmDto.builder()
                    .name("test" + i)
                    .releaseDate(LocalDate.now())
                    .description("desc" + i)
                    .build();

            filmStorage.create(film);
        }

        for (int i = 0; i < usersCount; i++) {
            User user = User.builder()
                    .email(String.format("testuser%d@mail.ru", i))
                    .login(String.format("testlogin%d", i))
                    .name(String.format("testname%d", i))
                    .birthday(LocalDate.now())
                    .build();

            userStorage.create(user);
        }

        User[] users = new User[usersCount];
        userStorage.getAll().toArray(users);

        FilmDto[] films = new FilmDto[filmsCount];
        filmStorage.getAll().toArray(films);

        filmStorage.addLike(films[2], users[0]);
        filmStorage.addLike(films[3], users[0]);
        filmStorage.addLike(films[2], users[1]);
        filmStorage.addLike(films[3], users[1]);
        filmStorage.addLike(films[2], users[2]);
        filmStorage.addLike(films[4], users[2]);

        Collection<FilmDto> topLikes = filmStorage.getTopLikes(topLikesSize);

        assertEquals(3, topLikes.size());

        assertThat(topLikes.toArray())
                .matches(top -> top[0].equals(films[2]))
                .matches(top -> top[1].equals(films[3]))
                .matches(top -> top[2].equals(films[4]));

    }
}