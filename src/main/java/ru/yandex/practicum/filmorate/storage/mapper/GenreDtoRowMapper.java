package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreDtoRowMapper implements RowMapper<GenreDto> {

    @Override
    public GenreDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        GenreDto dto = new GenreDto();
        dto.setId(rs.getLong("genre_id"));
        dto.setName(rs.getString("genre_name"));
        return dto;
    }
}
