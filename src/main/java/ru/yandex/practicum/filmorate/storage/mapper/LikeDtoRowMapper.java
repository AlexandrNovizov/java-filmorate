package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dto.LikeDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LikeDtoRowMapper implements RowMapper<LikeDto> {

    @Override
    public LikeDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        LikeDto dto = new LikeDto();
        dto.setFilmId(rs.getLong("film_id"));
        dto.setUserId(rs.getLong("user_id"));
        return dto;
    }
}
