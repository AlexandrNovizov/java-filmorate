package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreRowMapper implements RowMapper<Genre> {

    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        Genre.GenreBuilder builder = Genre.builder();

        long id = rs.getLong("genre_id");
        if (id == 0L) {
            builder.id(null);
        } else {
            builder.id(id);
        }

        builder.name(rs.getString("genre_name"));

        return builder.build();
    }
}
