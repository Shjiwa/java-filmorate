package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE");
        while (rowSet.next()) {
            Genre genre = Genre.builder()
                    .id(rowSet.getInt("GENRE_ID"))
                    .name(rowSet.getString("GENRE_NAME"))
                    .build();
            genres.add(genre);
        }
        return genres;
    }

    @Override
    public Set<Genre> getGenre(Long id) {
        Set<Genre> genres = new HashSet<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM FILM_GENRE ORDER BY GENRE_ID");
        while (rowSet.next()) {
            if (rowSet.getLong("FILM_ID") == id) {
                genres.add(getGenreById(rowSet.getInt("GENRE_ID")));
            }
        }
        return genres;
    }

    @Override
    public Film addGenre(Film film) {
        film.getGenres().forEach(genre ->
                jdbcTemplate.update("INSERT INTO FILM_GENRE(GENRE_ID, FILM_ID) VALUES (?, ?)",
                genre.getId(), film.getId()));
        return film;
    }

    @Override
    public Film updateGenreForFilmInMemory(Film film) {
        if (film.getGenres() == null) {
            return film;
        }
        film.getGenres().forEach(genre -> genre.setName(getGenreById(genre.getId()).getName()));
        return film;
    }

    @Override
    public Film updateGenre(Film film) {
        jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE FILM_ID = ?", film.getId());
        addGenre(film);
        return film;
    }

    @Override
    public Genre getGenreById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM GENRE WHERE GENRE_ID = ?", this::rowMapToGenre, id);
        } catch (DataAccessException e) {
            throw new NotFoundException("Genre not found");
        }
    }

    private Genre rowMapToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("GENRE_ID"))
                .name(rs.getString("GENRE_NAME"))
                .build();
    }
}
