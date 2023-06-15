package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.ExceptionService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final ExceptionService exceptionService;

    @Override
    public List<Film> getFilms() {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * " +
                "FROM FILMS " +
                "LEFT JOIN FILM_GENRE FG on FILMS.FILM_ID = FG.FILM_ID " +
                "LEFT JOIN GENRE G2 on G2.GENRE_ID = FG.GENRE_ID " +
                "LEFT JOIN LIKES L on FILMS.FILM_ID = L.FILM_ID " +
                "JOIN MPA M on M.MPA_ID = FILMS.MPA_RATING_ID");
        return rowSetMapper(rowSet);
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");
        film.setId(simpleJdbcInsert.executeAndReturnKey(collectToMap(film)).longValue());
        film.setRating(film.getLikes().size());

        log.debug("Фильм: {} добавлен!", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE FILMS " +
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
        if (rowsCount > 0) {
            log.debug("Фильм: {} обновлен!", film.getName());
            return film;
        }
        log.error("Not found");
        exceptionService.throwNotFound();
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * " +
                    "FROM FILMS " +
                    "LEFT JOIN FILM_GENRE FG on FILMS.FILM_ID = FG.FILM_ID " +
                    "LEFT JOIN GENRE G2 on G2.GENRE_ID = FG.GENRE_ID " +
                    "LEFT JOIN LIKES L on FILMS.FILM_ID = L.FILM_ID " +
                    "JOIN MPA M on M.MPA_ID = FILMS.MPA_RATING_ID " +
                    "WHERE FILMS.FILM_ID = ?", id);
            return rowSetMapper(rowSet).get(0);
        } catch (DataAccessException e) {
            log.error("Not found");
            throw new NotFoundException("Film not found");
        } catch (IndexOutOfBoundsException e) {
            log.error("Out of bounds");
            throw new NotFoundException("Not found");
        }
    }

    @Override
    public Film addLike(Long id, Long userId) {
        jdbcTemplate.update("INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?,?)", id, userId);
        log.debug("Пользователь с id: {} поставил лайк фильму с id: {}", userId, id);
        return getFilmById(id);
    }

    @Override
    public Film deleteLike(Long id, Long userId) {
        jdbcTemplate.update("DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?", id, userId);
        log.debug("Пользователь с id: {} удалил лайк к фильму с id: {}", userId, id);
        return getFilmById(id);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT *, COUNT(L.FILM_ID) AS film_count " +
                "FROM FILMS " +
                "         LEFT JOIN LIKES AS L ON FILMS.FILM_ID = L.FILM_ID " +
                "         LEFT JOIN FILM_GENRE FG on FILMS.FILM_ID = FG.FILM_ID " +
                "         LEFT JOIN GENRE G2 on G2.GENRE_ID = FG.GENRE_ID " +
                "         JOIN MPA M on M.MPA_ID = FILMS.MPA_RATING_ID " +
                "GROUP BY FILMS.FILM_ID, L.LIKE_ID " +
                "ORDER BY film_count DESC " +
                "LIMIT ?", count);
        List<Film> films = rowSetMapper(rowSet);
        return films;
    }

    private List<Film> rowSetMapper(SqlRowSet rowSet) {
        List<Film> films = new ArrayList<>();
        Set<Film> filmSet = new HashSet<>();
        Map<Long, Set<Genre>> genresMap = new HashMap<>();
        Map<Long, Set<Like>> likesMap = new HashMap<>();
        while (rowSet.next()) {
            Long filmId = rowSet.getLong("FILM_ID");
            Long userId = rowSet.getLong("USER_ID");
            Film film = new Film();
            Mpa mpa = Mpa.builder()
                    .id(rowSet.getLong("MPA_ID"))
                    .name(rowSet.getString("MPA_NAME"))
                    .build();
            try {
                Genre genre = Genre.builder()
                        .id(rowSet.getInt("GENRE_ID"))
                        .name(rowSet.getString("GENRE_NAME"))
                        .build();
                Like like = Like.builder()
                        .id(rowSet.getLong("LIKE_ID"))
                        .filmId(filmId)
                        .userId(userId)
                        .build();
                if (genresMap.containsKey(filmId) && genre.getName() != null) {
                    genresMap.get(filmId).add(genre);
                } else if (genre.getName() != null) {
                    Set<Genre> genreSet = new TreeSet<>(Comparator.comparingInt(Genre::getId));
                    genreSet.add(genre);
                    genresMap.put(filmId, genreSet);
                }
                if (likesMap.containsKey(filmId)) {
                    likesMap.get(filmId).add(like);
                } else if (userId != 0) {
                    Set<Like> likeSet = new HashSet<>();
                    likeSet.add(like);
                    likesMap.put(filmId, likeSet);
                }
            } catch (InvalidResultSetAccessException e) {
                log.debug("empty genre / like");
            }
            film.setId(filmId);
            film.setName(rowSet.getString("FILM_NAME"));
            film.setDescription(rowSet.getString("DESCRIPTION"));
            film.setReleaseDate(Objects.requireNonNull(rowSet.getDate("RELEASE_DATE")).toLocalDate());
            film.setDuration(rowSet.getInt("DURATION"));
            film.setRating(rowSet.getInt("RATING"));
            film.setMpa(mpa);
            filmSet.add(film);
        }
        for (Film film : filmSet) {
            Long filmId = film.getId();
            if (genresMap.containsKey(filmId)) {
                film.setGenres(genresMap.get(filmId));
            }
            if (likesMap.containsKey(filmId)) {
                film.setLikes(likesMap.get(filmId).stream().map(Like::getUserId).collect(Collectors.toSet()));
            }
            films.add(film);
        }
        return films;
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
