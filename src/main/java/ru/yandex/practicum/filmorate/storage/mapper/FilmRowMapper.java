package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film.FilmBuilder builder = Film.builder();
        builder.id(rs.getLong("film_id"));
        builder.name(rs.getString("film_name"));
        builder.description(rs.getString("description"));

        Timestamp releaseDate = rs.getTimestamp("release_date");
        builder.releaseDate(releaseDate.toLocalDateTime().toLocalDate());
        return builder.build();
    }
}
