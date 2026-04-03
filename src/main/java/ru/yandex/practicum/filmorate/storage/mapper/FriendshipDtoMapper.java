package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dto.FriendshipDto;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendshipDtoMapper implements RowMapper<FriendshipDto> {

    @Override
    public FriendshipDto mapRow(ResultSet rs, int rowNum) throws SQLException {

        FriendshipDto dto = new FriendshipDto();
        dto.setUserId(rs.getLong("user_id"));
        dto.setFriendId(rs.getLong("friend_id"));
        dto.setStatus(FriendshipStatus.valueOf(rs.getString("status_name")));

        return dto;
    }
}
