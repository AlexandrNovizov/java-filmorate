package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dto.FriendshipStatusDto;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;


public class FriendshipStatusMapper implements RowMapper<FriendshipStatusDto> {

    @Override
    public FriendshipStatusDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        FriendshipStatusDto dto = new FriendshipStatusDto();
        dto.setId(rs.getLong("status_id"));
        String statusString = rs.getString("status_name");
        dto.setStatus(FriendshipStatus.valueOf(statusString));
        return dto;
    }
}
