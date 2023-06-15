package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Long> getLikes(Long id) {
        Set<Long> likes = new HashSet<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM LIKES");
        while (rowSet.next()) {
            if (rowSet.getLong("FILM_ID") == id) {
                likes.add(rowSet.getLong("USER_ID"));
            }
        }
        return likes;
    }
}
