package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.util.*;

@Repository
public class DbGenreStorage extends BaseRepository<GenreDto> implements GenreStorage {


    public DbGenreStorage(JdbcTemplate jdbc, RowMapper<GenreDto> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<GenreDto> getAll() {

        String selectAllQuery = "SELECT * FROM genre";

        return findMany(selectAllQuery);
    }

    @Override
    public Optional<GenreDto> getById(long genreId) {
        String selectByIdQuery = "SELECT * FROM genre WHERE genre_id = ?";
        return findOne(selectByIdQuery, genreId);
    }

    @Override
    public Collection<GenreDto> getAllFromCollection(Collection<Long> genreIds) {
        String idPlaceholders = String.join(",", Collections.nCopies(genreIds.size(), "?"));
        String selectAllGenresInList = String.format("SELECT * FROM genre WHERE genre_id IN (%s)", idPlaceholders);

        return findMany(selectAllGenresInList, genreIds.toArray());
    }

    @Override
    public Optional<Long> checkGenreIds(Collection<Long> genreIds) {
        String idPlaceholders = String.join(
                ",", Collections.nCopies(genreIds.size(), "(?)")
        );

        String checkIdsQuery = String.format("SELECT id FROM (VALUES %s) AS input_ids(id) " +
                "WHERE NOT EXISTS (SELECT 1 FROM genre g WHERE g.genre_id = input_ids.id) LIMIT 1", idPlaceholders);

        Long missedId = null;
        try {
            missedId = jdbc.queryForObject(
                    checkIdsQuery,
                    Long.class,
                    genreIds.toArray()
            );
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }

        return Optional.of(missedId);
    }

    @Override
    public void createForFilm(Long filmId, Collection<Long> genreIds) {
        String insertQuery = "INSERT INTO film_genre(film_id, genre_id) VALUES %s";
        String placeholder = String.join(",", Collections.nCopies(genreIds.size(), "(" + filmId + ", ?)"));

        jdbc.update(String.format(insertQuery, placeholder), genreIds.toArray());
    }
}
