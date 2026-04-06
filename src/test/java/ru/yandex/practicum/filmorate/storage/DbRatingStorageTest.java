package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dto.RatingDto;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate.storage")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DbRatingStorageTest {

    private final DbRatingStorage storage;

    @Test
    void getAllShouldReturnAllRatings() {
        int expectedSize = 5;

        Collection<RatingDto> allRatings = storage.getAll();

        assertEquals(expectedSize, allRatings.size());
    }

    @Test
    void getByIdShouldReturnRatingWithGivenId() {
        long ratingId = 1L;
        String expectedName = "G";

        Optional<RatingDto> optRating = storage.getById(ratingId);

        assertThat(optRating)
                .isPresent()
                .hasValueSatisfying(receivedRating -> assertThat(receivedRating)
                        .hasFieldOrPropertyWithValue("name", expectedName)
                );
    }

    @Test
    void getByIdShouldReturnEmptyIfRatingWithGivenIdNotExists() {
        long ratingId = 1_000_000L;

        Optional<RatingDto> optRating = storage.getById(ratingId);

        assertThat(optRating)
                .isEmpty();
    }

}