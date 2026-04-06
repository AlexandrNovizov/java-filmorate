package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate.storage")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class DbGenreStorageTest {

    private final DbGenreStorage storage;

    @Test
    void testGetAllShouldReturnAllGenres() {
        int expectedGenresCount = 6;
        Collection<GenreDto> allGenres = storage.getAll();
        assertEquals(expectedGenresCount, allGenres.size());
    }

    @Test
    void testGetGenreByIdShouldReturnGenre() {
        String expectedName = "Комедия";
        long genreId = 1L;

        Optional<GenreDto> genre = storage.getById(genreId);

        assertThat(genre)
                .isPresent()
                .hasValueSatisfying(receivedGenre -> assertThat(receivedGenre)
                        .hasFieldOrPropertyWithValue("name", expectedName));
    }

    @Test
    void testGetGenreByIdShouldReturnEmptyIfNotFound() {
        long unExistingId = 999;

        Optional<GenreDto> genre = storage.getById(unExistingId);

        assertThat(genre)
                .isEmpty();
    }

    @Test
    void testCheckGenreIdsShouldReturnFirstUnExistentIds() {
        List<Long> ids = new ArrayList<>();
        long expected = 1_000_000L;
        ids.add(1L);
        ids.add(1_000_000L);
        ids.add(999_999L);

        Optional<Long> optId = storage.checkGenreIds(ids);

        assertThat(optId)
                .isPresent()
                .hasValueSatisfying(receivedId -> assertThat(receivedId).isEqualTo(expected));
    }

    @Test
    void testCheckGenreIdsShouldReturnEmptyIfAllGivenIdsExists() {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(4L);
        ids.add(3L);
        Optional<Long> optId = storage.checkGenreIds(ids);

        assertThat(optId)
                .isEmpty();
    }

    @Test
    void testGetAllFromCollectionReturnsOnlyGenresWithExistId() {
        int expectedSize = 2;
        String firstExpectedName = "Комедия";
        String secondExpectedName = "Триллер";
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(4L);
        ids.add(999_999L);
        Collection<GenreDto> allFromCollection = storage.getAllFromCollection(ids);

        assertThat(allFromCollection)
                .matches(collection -> collection.size() == expectedSize);

        GenreDto[] dtos = new GenreDto[expectedSize];
        allFromCollection.toArray(dtos);
        assertThat(dtos)
                .matches(array -> array[0].getName().equals(firstExpectedName))
                .matches(array -> array[1].getName().equals(secondExpectedName));

    }
}