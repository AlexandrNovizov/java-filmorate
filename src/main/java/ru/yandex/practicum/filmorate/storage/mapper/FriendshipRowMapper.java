package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendshipRowMapper implements RowMapper<Friendship> {

    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        Friendship entity = new Friendship();
        entity.setUserId(rs.getLong("user_id"));
        entity.setFriendId(rs.getLong("friend_id"));
        entity.setStatus(FriendshipStatus.valueOf(rs.getString("status_name")));

        return entity;
    }
}
