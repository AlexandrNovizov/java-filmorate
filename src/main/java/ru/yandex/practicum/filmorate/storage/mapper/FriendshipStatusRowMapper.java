package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.FriendshipStatusEnum;

import java.sql.ResultSet;
import java.sql.SQLException;


public class FriendshipStatusRowMapper implements RowMapper<FriendshipStatus> {

    @Override
    public FriendshipStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
        FriendshipStatus entity = new FriendshipStatus();
        entity.setId(rs.getLong("status_id"));
        String statusString = rs.getString("status_name");
        entity.setStatus(FriendshipStatusEnum.valueOf(statusString));
        return entity;
    }
}
