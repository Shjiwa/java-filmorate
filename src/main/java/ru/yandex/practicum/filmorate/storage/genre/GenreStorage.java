package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    List<Genre> getGenres();
    Set<Genre> getGenre(Long id);
    Film addGenre(Film film);
    Film updateGenreForFilmInMemory(Film film);
    Film updateGenre(Film film);
    Genre getGenreById(int id);
}
