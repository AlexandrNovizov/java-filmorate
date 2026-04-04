package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.RatingDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingRowMapper implements RowMapper<RatingDto> {

    @Override
    public RatingDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        RatingDto dto = new RatingDto();
        dto.setId(rs.getLong("rating_id"));
        dto.setName(rs.getString("rating_name"));
        return dto;
    }
}
