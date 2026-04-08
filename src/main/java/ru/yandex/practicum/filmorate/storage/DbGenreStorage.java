package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.FilmGenreDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.mapper.FilmGenreMapper;
import ru.yandex.practicum.filmorate.dto.mapper.GenreDtoMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.FilmGenreRowMapper;

import java.util.*;

@Repository
public class DbGenreStorage extends BaseRepository<Genre> implements GenreStorage {


    public DbGenreStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<GenreDto> getAll() {

        String selectAllQuery = "SELECT * FROM genre";

        return findMany(selectAllQuery).stream().map(GenreDtoMapper::mapToGenreDto).toList();
    }

    @Override
    public Optional<GenreDto> getById(long genreId) {
        String selectByIdQuery = "SELECT * FROM genre WHERE genre_id = ?";
        Optional<Genre> optGenre = findOne(selectByIdQuery, genreId);
        return optGenre.map(GenreDtoMapper::mapToGenreDto);

    }

    @Override
    public Collection<GenreDto> getAllFromCollection(Collection<Long> genreIds) {
        String idPlaceholders = String.join(",", Collections.nCopies(genreIds.size(), "?"));
        String selectAllGenresInList = String.format("SELECT * FROM genre WHERE genre_id IN (%s)", idPlaceholders);

        return findMany(selectAllGenresInList, genreIds.toArray()).stream()
                .map(GenreDtoMapper::mapToGenreDto)
                .toList();
    }

    @Override
    public Optional<Long> checkGenreIds(Collection<Long> genreIds) {
        if (genreIds.isEmpty()) {
            return Optional.empty();
        }
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

    @Override
    public Map<Long, List<GenreDto>> allGenresByFilms() {
        String selectGenresQuery = "SELECT g.genre_id AS genre_id, film_id, genre_name FROM genre g JOIN film_genre fg " +
                "ON g.genre_id = fg.genre_id";

        List<FilmGenreDto> genres = jdbc.query(selectGenresQuery, new FilmGenreRowMapper()).stream()
                .map(FilmGenreMapper::mapToFilmGenreDto)
                .toList();

        Map<Long, List<GenreDto>> map = new HashMap<>();

        for (FilmGenreDto filmGenreDtos : genres) {
            GenreDto genreToAddInMap = new GenreDto();
            genreToAddInMap.setId(filmGenreDtos.getGenreId());
            genreToAddInMap.setName(filmGenreDtos.getGenreName());
            map.computeIfAbsent(filmGenreDtos.getFilmId(), ignored -> new ArrayList<>()).add(genreToAddInMap);
        }

        return map;
    }
}
