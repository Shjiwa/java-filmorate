package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;
    @Override
    public List<Mpa> getMpaList() {
        List<Mpa> mpas = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM MPA");
        while (rowSet.next()) {
            Mpa mpa = Mpa.builder()
                    .id(rowSet.getLong("MPA_ID"))
                    .name(rowSet.getString("MPA_NAME"))
                    .build();
            mpas.add(mpa);
        }
        return mpas;
    }

    @Override
    public Mpa getMpa(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM MPA WHERE MPA_ID = ?", this::rowMapToMpa, id);
        } catch (DataAccessException e) {
            throw new NotFoundException("Mpa not found");
        }
    }

    @Override
    public Mpa addToFilm(Film film) {
        getMpaList().forEach(mpa -> {
            if (film.getMpa().getId().equals(mpa.getId())) {
                film.setMpa(mpa);
            }
        });
        return film.getMpa();
    }

    private Mpa rowMapToMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("MPA_ID"))
                .name(rs.getString("MPA_NAME"))
                .build();
    }
}
