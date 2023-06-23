package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Long filmId = 1L;

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(filmId++);
        film.setRating(film.getLikes().size());
        films.put(film.getId(), film);
        log.info("Фильм {} добавлен", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        film.setRating(film.getLikes().size());
        films.put(film.getId(), film);
        log.info("Фильм c id {} обновлен", film.getId());
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        return films.get(id);
    }

    @Override
    public Film addLike(Long id, Long userId) {
        films.get(id).getLikes().add(userId);
        films.get(id).setRating(films.get(id).getLikes().size());
        log.info("Пользователь с id: {} поставил лайк фильму с id: {}", userId, id);
        return films.get(id);
    }

    @Override
    public Film deleteLike(Long id, Long userId) {
        films.get(id).getLikes().remove(userId);
        films.get(id).setRating(films.get(id).getLikes().size());
        log.info("Пользователю с id: {} больше не нравится фильм с id: {}", userId, id);
        return films.get(id);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return getFilms().stream()
                .sorted((film, t1) -> t1.getRating() - film.getRating())
                .limit(count)
                .collect(Collectors.toList()
                );
    }
}
