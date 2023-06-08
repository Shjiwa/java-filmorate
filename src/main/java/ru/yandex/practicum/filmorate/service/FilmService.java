package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;


    public Film addLike(Long id, Long userId) {
        filmStorage.addLike(id, userId);
        return filmStorage.getFilmById(id);
    }

    public Film deleteLike(Long id, Long userId) {
        filmStorage.deleteLike(id, userId);
        return filmStorage.getFilmById(id);
    }

    public List<Film> getTop10Films(int count) {
        return filmStorage.getTop10Films(count);
    }
}

