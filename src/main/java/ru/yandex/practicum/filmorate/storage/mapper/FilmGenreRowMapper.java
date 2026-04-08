package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmGenreRowMapper implements RowMapper<FilmGenre> {

    @Override
    public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmGenre entity = new FilmGenre();
        entity.setFilmId(rs.getLong("film_id"));
        entity.setGenreId(rs.getLong("genre_id"));
        entity.setGenreName(rs.getString("genre_name"));
        return entity;
    }
}
