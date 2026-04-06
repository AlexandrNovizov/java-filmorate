package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dto.FilmGenreDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmGenreDtoRowMapper implements RowMapper<FilmGenreDto> {

    @Override
    public FilmGenreDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmGenreDto dto = new FilmGenreDto();
        dto.setFilmId(rs.getLong("film_id"));
        dto.setGenreId(rs.getLong("genre_id"));
        return dto;
    }
}
