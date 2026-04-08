package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingRowMapper implements RowMapper<Rating> {

    @Override
    public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
        Rating entity = new Rating();
        entity.setId(rs.getLong("rating_id"));
        entity.setName(rs.getString("rating_name"));
        return entity;
    }
}
