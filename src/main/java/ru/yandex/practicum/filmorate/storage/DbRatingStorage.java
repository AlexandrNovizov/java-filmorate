package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.dto.mapper.RatingDtoMapper;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

@Repository
public class DbRatingStorage extends BaseRepository<Rating> implements RatingStorage {

    private static final String SELECT_ALL_RATINGS_QUERY = "SELECT * FROM rating";
    private static final String SELECT_RATING_BY_ID_QUERY = "SELECT * FROM rating WHERE rating_id = ?";

    public DbRatingStorage(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<RatingDto> getAll() {
        return findMany(SELECT_ALL_RATINGS_QUERY).stream()
                .map(RatingDtoMapper::mapToRatingDto)
                .toList();
    }

    @Override
    public Optional<RatingDto> getById(Long ratingId) {
        return findOne(SELECT_RATING_BY_ID_QUERY, ratingId).map(RatingDtoMapper::mapToRatingDto);
    }
}
