package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Long filmId = 1L;

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Map<Long, Film> getMap() {
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(filmId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        return films.get(id);
    }

    @Override
    public Film addLike(Long id, Long userId) {
        films.get(id).getLikes().add(userId);
        return films.get(id);
    }

    @Override
    public Film deleteLike(Long id, Long userId) {
        films.get(id).getLikes().remove(userId);
        return films.get(id);
    }

    @Override
    public List<Film> getTop10Films(int count) {
        return getFilms().stream()
                .sorted((film, t1) -> t1.getLikes().size() - film.getLikes().size())
                .limit(count)
                .collect(Collectors.toList()
                );
    }
}
