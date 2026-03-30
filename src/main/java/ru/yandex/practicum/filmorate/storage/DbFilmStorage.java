package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Optional;

@Repository("database")
public class DbFilmStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String SELECT_ALL_QUERY = "SELECT * FROM film";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM film WHERE film_id = ?";
    private static final String SELECT_LIKES_BY_FILM_ID_QUERY = "SELECT user_id FROM \"like\" WHERE film_id = ?";

    private static final String INSERT_FILM_QUERY = "INSERT INTO film " +
            "(film_name, rating_id, description, release_date, duration) VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_FILM_QUERY = "UPDATE film SET " +
            "film_name = ?, rating_id = ?, description = ?, release_date = ?, duration = ? WHERE film_id = ?";

    private static final String ADD_LIKE_QUERY = "INSERT INTO \"like\" (user_id, film_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DROP FROM \"like\" WHERE user_id = ? AND film_id = ?";

    public DbFilmStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> getAll() {
        return findMany(SELECT_ALL_QUERY);
    }

    @Override
    public Optional<Film> getById(Long id) {
        Optional<Film> optFilm = findOne(SELECT_BY_ID_QUERY, id);
        optFilm.ifPresent(film -> film.setLikes(getLikesForFilmId(id)));
        return optFilm;
    }

    @Override
    public Film create(Film object) {
        long id = insert(
                INSERT_FILM_QUERY,
                object.getName(),
                null,
                object.getDescription(),
                Timestamp.from(object.getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                object.getDuration()
        );
        object.setId(id);
        return object;
    }

    @Override
    public Film update(Film object) {
        update(
                UPDATE_FILM_QUERY,
                object.getName(),
                null,
                object.getDescription(),
                Timestamp.from(object.getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                object.getDuration(),
                object.getId()
        );
        return object;
    }

    @Override
    public void addLike(Film film, User user) {
        if (!film.getLikes().contains(user.getId())) {
            update(ADD_LIKE_QUERY, user.getId(), film.getId());
            film.getLikes().add(user.getId());
        }
    }

    @Override
    public void removeLike(Film film, User user) {
        if (film.getLikes().contains(user.getId())) {
            update(REMOVE_LIKE_QUERY, user.getId(), film.getId());
            film.getLikes().remove(user.getId());
        }
    }

    private Collection<Long> getLikesForFilmId(Long filmId) {
        return jdbc.queryForList(SELECT_LIKES_BY_FILM_ID_QUERY, Long.class, filmId);
    }
}
