package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User.UserBuilder builder = User.builder();

        builder.id(rs.getLong("user_id"));
        builder.email(rs.getString("email"));
        builder.name(rs.getString("name"));
        builder.login(rs.getString("login"));

        Timestamp birthday = rs.getTimestamp("birthday");
        builder.birthday(birthday.toLocalDateTime().toLocalDate());

        return builder.build();
    }
}
