package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LikeRowMapper implements RowMapper<Like> {

    @Override
    public Like mapRow(ResultSet rs, int rowNum) throws SQLException {
        Like entity = new Like();
        entity.setFilmId(rs.getLong("film_id"));
        entity.setUserId(rs.getLong("user_id"));
        return entity;
    }
}
