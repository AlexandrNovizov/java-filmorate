package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.FriendshipStatusDto;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.FriendshipStatusMapper;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.*;

@Repository("userDB")
public class DbUserStorage extends BaseRepository<User> implements UserStorage {

    private static final String SELECT_ALL_QUERY = "SELECT * FROM \"user\"";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM \"user\" WHERE user_id = ?";
    private static final String SELECT_FRIENDS_BY_ID_QUERY = "SELECT friend_id FROM friend " +
            "WHERE user_id = ? AND status_id IN (SELECT status_id FROM status WHERE status_name <> 'REJECTED')";

    private static final String INSERT_USER_QUERY = "INSERT INTO \"user\"(email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE \"user\" SET " +
            "email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";

    private static final String SELECT_ALL_FRIENDS_QUERY = "SELECT * FROM \"user\" WHERE user_id IN (%s)";
    private static final String SELECT_COMMON_FRIENDS_QUERY = "SELECT user_id, email, login, name, birthday " +
            "FROM (SELECT friend_id FROM friend WHERE user_id = ? AND status_id IN ( " +
            "SELECT status_id FROM status WHERE status_name <> 'REJECTED')) AS f1 " +
            "INNER JOIN (" +
            "SELECT friend_id FROM friend WHERE user_id = ? AND status_id IN (" +
            "SELECT status_id FROM status WHERE status_name <> 'REJECTED')) AS f2 " +
            "ON f1.friend_id = f2.friend_id " +
            "INNER JOIN \"user\" ON \"user\".user_id = f1.friend_id";

    private static final String SELECT_FRIENDSHIP_STATUSES_QUERY = "SELECT * FROM status";
    private static final String ADD_TO_FRIENDS_QUERY = "INSERT INTO friend(user_id, friend_id, status_id) VALUES" +
            " (?, ?, ?)";
    private static final String REMOVE_FROM_FRIENDS_QUERY = "DELETE FROM friend " +
            "WHERE user_id = ? AND friend_id = ?";

    private static final FriendshipStatusMapper friendshipStatusMapper = new FriendshipStatusMapper();

    public DbUserStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> getAll() {
        List<User> allUsers = findMany(SELECT_ALL_QUERY);

        for (var user: allUsers) {
            user.setFriends(jdbc.queryForList(SELECT_FRIENDS_BY_ID_QUERY, Long.class, user.getId()));
        }
        return allUsers;
    }

    @Override
    public Optional<User> getById(Long id) {
        Optional<User> optUser = findOne(SELECT_BY_ID_QUERY, id);
        optUser.ifPresent(user -> user.setFriends(getFriendsForUserId(id)));
        return optUser;
    }

    @Override
    public User create(User object) {
        Long id = insert(
                INSERT_USER_QUERY,
                object.getEmail(),
                object.getLogin(),
                object.getName(),
                Timestamp.from(object.getBirthday().atStartOfDay(ZoneId.systemDefault()).toInstant())
        );
        object.setId(id);
        return object;
    }

    @Override
    public User update(User object) {
        update(
                UPDATE_USER_QUERY,
                object.getEmail(),
                object.getLogin(),
                object.getName(),
                Timestamp.from(object.getBirthday().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                object.getId()
        );
        return object;
    }

    @Override
    public void addToFriends(User user1, User user2) {
        Map<FriendshipStatus, Long> statuses = getAllStatuses();

        jdbc.update(
                ADD_TO_FRIENDS_QUERY,
                user1.getId(),
                user2.getId(),
                statuses.get(FriendshipStatus.PENDING)
        );

        user1.getFriends().add(user2.getId());
    }

    @Override
    public void removeFromFriends(User user1, User user2) {
        jdbc.update(
                REMOVE_FROM_FRIENDS_QUERY,
                user1.getId(),
                user2.getId()
        );

        user1.getFriends().remove(user2.getId());
    }

    @Override
    public Collection<User> getFriends(User user) {
        String ids = String.join(",", Collections.nCopies(user.getFriends().size(), "?"));

        return jdbc.query(
                String.format(SELECT_ALL_FRIENDS_QUERY, ids),
                mapper,
                user.getFriends().toArray()
        );
    }

    @Override
    public Collection<User> getCommonFriends(User user1, User user2) {
        return jdbc.query(
                SELECT_COMMON_FRIENDS_QUERY,
                mapper,
                user1.getId(),
                user2.getId()
        );
    }

    private Collection<Long> getFriendsForUserId(Long userId) {
        return jdbc.queryForList(SELECT_FRIENDS_BY_ID_QUERY, Long.class, userId);
    }

    private Map<FriendshipStatus, Long> getAllStatuses() {
        Map<FriendshipStatus, Long> result = new HashMap<>();
        List<FriendshipStatusDto> dtos = jdbc.query(SELECT_FRIENDSHIP_STATUSES_QUERY, friendshipStatusMapper);
        for (var dto: dtos) {
            result.put(dto.getStatus(), dto.getId());
        }

        return result;
    }
}
