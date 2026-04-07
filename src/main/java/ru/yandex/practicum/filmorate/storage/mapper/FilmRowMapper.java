package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.RatingDto;
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
        builder.duration(rs.getInt("duration"));
        long ratingId = rs.getLong("mpa_id");
        if (ratingId == 0L) {
            builder.ratingId(null);
        } else {
            builder.ratingId(ratingId);
        }
        builder.ratingName(rs.getString("mpa_name"));
        Timestamp releaseDate = rs.getTimestamp("release_date");
        builder.releaseDate(releaseDate.toLocalDateTime().toLocalDate());
        return builder.build();
    }
}
