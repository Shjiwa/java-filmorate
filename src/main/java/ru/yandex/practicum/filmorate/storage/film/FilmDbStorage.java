package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ExceptionService;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final ExceptionService exceptionService;

    @Override
    public List<Film> getFilms() {
        List<Film> films = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM FILMS");
        while (rowSet.next()) {
            Film film = new Film();
            film.setId(rowSet.getLong("FILM_ID"));
            film.setName(rowSet.getString("FILM_NAME"));
            film.setDescription(rowSet.getString("DESCRIPTION"));
            film.setReleaseDate(Objects.requireNonNull(rowSet.getDate("RELEASE_DATE")).toLocalDate());
            film.setDuration(rowSet.getInt("DURATION"));
            film.setRating(rowSet.getInt("RATING"));
            film.setMpa(mpaDbStorage.getMpa(rowSet.getInt("MPA_RATING_ID")));
            film.setGenres(genreDbStorage.getGenre(film.getId()));
            film.setLikes(likeDbStorage.getLikes(film.getId()));

            films.add(film);
        }
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");
        film.setId(simpleJdbcInsert.executeAndReturnKey(collectToMap(film)).longValue());
        film.setRating(film.getLikes().size());
        mpaDbStorage.addToFilm(film);
        genreDbStorage.addGenre(film);

        log.info("Фильм: {} добавлен!", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films " +
                     "SET FILM_NAME = ?," +
                         "DESCRIPTION = ?," +
                         "RELEASE_DATE = ?," +
                         "DURATION = ?," +
                         "RATING = ?," +
                         "MPA_RATING_ID = ? " +
                     "WHERE FILM_ID = ?";
        film.setRating(film.getLikes().size());
        int rowsCount = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRating(), film.getMpa().getId(), film.getId());
        mpaDbStorage.addToFilm(film);
        genreDbStorage.updateGenre(film);
        genreDbStorage.updateGenreForFilmInMemory(film);
        film.setGenres(genreDbStorage.getGenre(film.getId()));
        if (rowsCount > 0) {
            log.info("Фильм: {} обновлен!", film.getName());
            return film;
        }
        log.error("Not found");
        exceptionService.throwNotFound();
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM FILMS WHERE FILM_ID = ?", this::rowMapToFilm, id);
        } catch (DataAccessException e) {
            log.error("Not found");
            throw new NotFoundException("Film not found");
        }
    }

    @Override
    public Film addLike(Long id, Long userId) {
        jdbcTemplate.update("INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?,?)", id, userId);
        log.info("Пользователь с id: {} поставил лайк фильму с id: {}", userId, id);
        return getFilmById(id);
    }

    @Override
    public Film deleteLike(Long id, Long userId) {
        jdbcTemplate.update("DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?", id, userId);
        log.info("Пользователь с id: {} удалил лайк к фильму с id: {}", userId, id);
        return getFilmById(id);
    }

    @Override
    public List<Film> getTop10Films(int count) {
        String sql = "SELECT FILMS.*, COUNT(l.FILM_ID) AS film_count " +
                     "FROM FILMS " +
                     "LEFT JOIN LIKES AS l ON FILMS.FILM_ID = l.FILM_ID " +
                     "GROUP BY FILMS.FILM_ID " +
                     "ORDER BY film_count DESC " +
                     "LIMIT ?";
        return jdbcTemplate.query(sql, this::rowMapToFilm, count);
    }

    private Film rowMapToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("FILM_ID"));
        film.setName(rs.getString("FILM_NAME"));
        film.setDescription(rs.getString("DESCRIPTION"));
        film.setReleaseDate(rs.getDate("RELEASE_DATE").toLocalDate());
        film.setDuration(rs.getInt("DURATION"));
        film.setRating(rs.getInt("RATING"));
        film.setMpa(mpaDbStorage.getMpa(rs.getInt("MPA_RATING_ID")));
        film.setLikes(likeDbStorage.getLikes(film.getId()));
        film.setGenres(genreDbStorage.getGenre(film.getId()));
        return film;
    }

    private Map<String, Object> collectToMap(Film film) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("FILM_NAME", film.getName());
        fields.put("DESCRIPTION", film.getDescription());
        fields.put("RELEASE_DATE", film.getReleaseDate());
        fields.put("DURATION", film.getDuration());
        film.setRating(film.getLikes().size());
        fields.put("RATING", film.getRating());
        fields.put("MPA_RATING_ID", film.getMpa().getId());
        return fields;
    }
}
