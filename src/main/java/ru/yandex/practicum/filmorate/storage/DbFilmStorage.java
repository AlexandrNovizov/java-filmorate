package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.dto.mapper.FilmDtoMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.FilmGenreDtoRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreDtoRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.LikeDtoRowMapper;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class DbFilmStorage extends BaseRepository<Film> implements FilmStorage {

    private final RatingStorage ratingStorage;

    private static final String SELECT_ALL_QUERY =
            "SELECT film_id, film_name, description, duration, " +
            "NULLIF(f.rating_id, 0) AS mpa_id, release_date, r.rating_name AS mpa_name " +
            "FROM film AS f LEFT JOIN rating AS r ON f.rating_id = r.rating_id";
    private static final String SELECT_BY_ID_QUERY =
            "SELECT film_id, film_name, description, duration, " +
                    "NULLIF(f.rating_id, 0) AS mpa_id, release_date, r.rating_name AS mpa_name " +
                    "FROM film AS f LEFT JOIN rating AS r ON f.rating_id = r.rating_id WHERE film_id = ?";
    private static final String SELECT_LIKES_BY_FILM_ID_QUERY = "SELECT user_id FROM \"like\" WHERE film_id = ?";

    private static final String INSERT_FILM_QUERY = "INSERT INTO film " +
            "(film_name, rating_id, description, release_date, duration) VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_FILM_QUERY = "UPDATE film SET " +
            "film_name = ?, rating_id = ?, description = ?, release_date = ?, duration = ? WHERE film_id = ?";

    private static final String ADD_LIKE_QUERY = "INSERT INTO \"like\" (user_id, film_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM \"like\" WHERE user_id = ? AND film_id = ?";

    public DbFilmStorage(
            JdbcTemplate jdbc,
            RowMapper<Film> filmMapper,
            RowMapper<RatingDto> ratingMapper) {

        super(jdbc, filmMapper);
        ratingStorage = new DbRatingStorage(jdbc, ratingMapper);
    }

    @Override
    public Collection<FilmDto> getAll() {
        Map<Long, FilmDto> filmsMap = createFilmsMap(findMany(SELECT_ALL_QUERY));
        String getAllLikesQuery = "SELECT * FROM \"like\"";

        List<LikeDto> likes = jdbc.query(getAllLikesQuery, new LikeDtoRowMapper());

        for (LikeDto like : likes) {
            filmsMap.get(like.getFilmId()).getLikes().add(like.getUserId());
        }

        Map<Long, GenreDto> genresMap = createGenresMap(
                jdbc.query("SELECT * FROM genre", new GenreDtoRowMapper())
        );

        List<FilmGenreDto> genres = jdbc.query("SELECT * FROM film_genre", new FilmGenreDtoRowMapper());

        for (FilmGenreDto genre : genres) {
            filmsMap.get(genre.getFilmId()).getGenres().add(genresMap.get(genre.getGenreId()));
        }

        return filmsMap.values();
    }

    @Override
    public Optional<FilmDto> getById(Long id) {
        Optional<Film> film = findOne(SELECT_BY_ID_QUERY, id);

        if (film.isEmpty()) {
            return Optional.empty();
        }

        FilmDto dto = FilmDtoMapper.mapToFilmDto(film.get());

        if (film.get().getRatingName() == null && film.get().getRatingId() != null) {
            throw new NotFoundException(String.format("Рейтинг с id %d не найден", film.get().getRatingId()));
        }

        dto.setLikes(getLikesForFilmId(id));
        dto.setGenres(getGenresForFilmId(id));

        return Optional.of(dto);
    }

    @Override
    public FilmDto create(FilmDto object) {
        RatingDto rating = null;
        if (object.getMpa() != null) {
            rating = ratingStorage.getById(object.getMpa().getId()).orElseThrow(
                    () -> new NotFoundException(String.format("Рейтинг с id %d не найден", object.getMpa().getId()))
            );
            object.setMpa(rating);
        }
        long id = insert(
                INSERT_FILM_QUERY,
                object.getName(),
                rating == null ? null : rating.getId(),
                object.getDescription(),
                Timestamp.from(object.getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                object.getDuration()
        );
        object.setId(id);

        return object;
    }

    @Override
    public FilmDto update(FilmDto object) {
        Long ratingId = null;
        if (object.getMpa() != null && object.getMpa().getId() != null) {
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
    public void addLike(FilmDto film, User user) {
        if (!film.getLikes().contains(user.getId())) {
            update(ADD_LIKE_QUERY, user.getId(), film.getId());
            film.getLikes().add(user.getId());
        }
    }

    @Override
    public void removeLike(FilmDto film, User user) {
        if (film.getLikes().contains(user.getId())) {
            update(REMOVE_LIKE_QUERY, user.getId(), film.getId());
            film.getLikes().remove(user.getId());
        }
    }

    @Override
    public Collection<FilmDto> getTopLiked(int size) {
        String conidtion = " WHERE film_id IN " +
        "(SELECT film_id FROM \"like\" " +
                "GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?)";
        String getTopQuery = SELECT_ALL_QUERY + conidtion;

        List<Film> filmsList = findMany(getTopQuery, size);
        Map<Long, FilmDto> films = createFilmsMap(filmsList);

        String getLikesQuery = "SELECT * FROM \"like\" WHERE film_id IN (%s)";

        String filmIds  = films.values().stream()
                .map(FilmDto::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        List<LikeDto> likes = jdbc.query(String.format(getLikesQuery, filmIds), new LikeDtoRowMapper());

        for (LikeDto dto : likes) {
            if (dto.getFilmId() != null && dto.getUserId() != null) {
                films.get(dto.getFilmId()).getLikes().add(dto.getUserId());
            }
        }

        return films.values().stream().sorted(
                Comparator.comparingInt(film -> ((FilmDto) film).getLikes().size()).reversed()
        ).toList();

    }

    private List<Long> getLikesForFilmId(Long filmId) {
        return jdbc.queryForList(SELECT_LIKES_BY_FILM_ID_QUERY, Long.class, filmId);
    }

    private List<GenreDto> getGenresForFilmId(Long filmId) {

        var getGenreIdsQuery = "SELECT * FROM genre WHERE genre_id IN " +
                "(SELECT genre_id FROM film_genre WHERE film_id = ?)";

        return jdbc.query(getGenreIdsQuery, new GenreDtoRowMapper(), filmId);
    }

    private Map<Long, FilmDto> createFilmsMap(List<Film> films) {
        Map<Long, FilmDto> map = new HashMap<>();

        for (Film film : films) {
            map.put(film.getId(), FilmDtoMapper.mapToFilmDto(film));
        }

        return map;
    }

    private Map<Long, GenreDto> createGenresMap(List<GenreDto> genres) {
        Map<Long, GenreDto> map = new HashMap<>();

        for (GenreDto genre : genres) {
            map.put(genre.getId(), genre);
        }

        return map;
    }
}
