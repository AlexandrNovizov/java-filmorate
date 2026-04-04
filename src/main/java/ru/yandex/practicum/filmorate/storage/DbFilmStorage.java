package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.LikeDto;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.LikeDtoRowMapper;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Repository("filmDB")
public class DbFilmStorage extends BaseRepository<Film> implements FilmStorage {

    private final RatingStorage ratingStorage;

    private static final String SELECT_ALL_QUERY = "SELECT * FROM film";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM film WHERE film_id = ?";
    private static final String SELECT_LIKES_BY_FILM_ID_QUERY = "SELECT user_id FROM \"like\" WHERE film_id = ?";

    private static final String INSERT_FILM_QUERY = "INSERT INTO film " +
            "(film_name, rating_id, description, release_date, duration) VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_FILM_QUERY = "UPDATE film SET " +
            "film_name = ?, rating_id = ?, description = ?, release_date = ?, duration = ? WHERE film_id = ?";

    private static final String ADD_LIKE_QUERY = "INSERT INTO \"like\" (user_id, film_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM \"like\" WHERE user_id = ? AND film_id = ?";

    public DbFilmStorage(JdbcTemplate jdbc, RowMapper<Film> filmMapper, RowMapper<RatingDto> ratingMapper) {
        super(jdbc, filmMapper);
        ratingStorage = new DbRatingStorage(jdbc, ratingMapper);
    }

    @Override
    public Collection<Film> getAll() {
        Map<Long, Film> filmsMap = createMap(findMany(SELECT_ALL_QUERY));
        String getAllLikesQuery = "SELECT * FROM \"like\"";

        List<LikeDto> likes = jdbc.query(getAllLikesQuery, new LikeDtoRowMapper());

        for (LikeDto like : likes) {
            filmsMap.get(like.getFilmId()).getLikes().add(like.getUserId());
        }

        return filmsMap.values();
    }

    @Override
    public Optional<Film> getById(Long id) {
        Optional<Film> optFilm = findOne(SELECT_BY_ID_QUERY, id);
        optFilm.ifPresent(film -> film.setLikes(getLikesForFilmId(id)));
        return optFilm;
    }

    @Override
    public Film create(Film object) {
        Long ratingId = null;
        if (object.getMpa() != null) {
            ratingId = ratingStorage.getById(object.getMpa().getId()).orElseThrow(
                    () -> new NotFoundException(String.format("Рейтинг с id %d не найден", object.getMpa().getId()))
            ).getId();
        }
        long id = insert(
                INSERT_FILM_QUERY,
                object.getName(),
                ratingId,
                object.getDescription(),
                Timestamp.from(object.getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                object.getDuration()
        );
        object.setId(id);
        return object;
    }

    @Override
    public Film update(Film object) {
        Long ratingId = null;
        if (object.getMpa() != null) {
            ratingId = ratingStorage.getById(object.getMpa().getId()).orElseThrow(
                    () -> new NotFoundException(String.format("Рейтинг с id %d не найден", object.getMpa().getId()))
            ).getId();
        }
        update(
                UPDATE_FILM_QUERY,
                object.getName(),
                ratingId,
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

    @Override
    public Collection<Film> getTopLikes(int size) {
        String getTopQuery = "SELECT * FROM film WHERE film_id IN " +
                "(SELECT film_id FROM \"like\" " +
                "GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?)";

        List<Film> filmsList = findMany(getTopQuery, size);
        Map<Long, Film> films = createMap(filmsList);

        String getLikesQuery = "SELECT * FROM \"like\" WHERE film_id IN (%s)";

        String filmIds  = films.values().stream()
                .map(Film::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        List<LikeDto> likes = jdbc.query(String.format(getLikesQuery, filmIds), new LikeDtoRowMapper());

        for (LikeDto dto : likes) {
            films.get(dto.getFilmId()).getLikes().add(dto.getUserId());
        }

        return films.values().stream().sorted(
                Comparator.comparingInt(film -> ((Film) film).getLikes().size()).reversed()
        ).toList();

    }

    private Collection<Long> getLikesForFilmId(Long filmId) {
        return jdbc.queryForList(SELECT_LIKES_BY_FILM_ID_QUERY, Long.class, filmId);
    }

    private Map<Long, Film> createMap(List<Film> films) {
        Map<Long, Film> map = new HashMap<>();

        for (Film film : films) {
            map.put(film.getId(), film);
        }

        return map;
    }
}
