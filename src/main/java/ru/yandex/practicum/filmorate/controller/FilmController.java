package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ExceptionService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmService filmService;
    private final ExceptionService exceptionService;

    @GetMapping
    public List<Film> getFilms() {
        log.info("Поступил запрос на получение списка фильмов");
        return filmStorage.getFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Фильм {} добавлен", film.getName());
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (filmStorage.getMap().get(film.getId()) == null) {
            log.error("Film not found");
            exceptionService.throwNotFound();
        }
        log.info("Фильм c id {} обновлен", film.getId());
        return filmStorage.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Long id, @PathVariable Long userId) {
        if (filmStorage.getMap().get(id) == null || userStorage.getMap().get(userId) == null) {
            log.error("Film or user not found");
            exceptionService.throwNotFound();
        }
        log.info("Пользователь {} поставил лайк фильму {}",
                userStorage.getUserById(userId).getName(), filmStorage.getFilmById(id).getName());
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        if (filmStorage.getMap().get(id) == null || userStorage.getMap().get(userId) == null
                || !filmStorage.getFilmById(id).getLikes().contains(userId)) {
            log.error("Film or user not found, or there is no like to delete");
            exceptionService.throwNotFound();
        }
        log.info("Пользователю {} больше не нравится фильм {}",
                userStorage.getUserById(userId).getName(), filmStorage.getFilmById(id).getName());
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        if (filmStorage.getMap().get(id) == null) {
            log.error("Film not found");
            exceptionService.throwNotFound();
        }
        log.info("Поступил запрос на получение фильма с id {}", id);
        return filmStorage.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getTop10Films(@RequestParam(defaultValue = "10") int count) {
        if (count < 0) {
            log.error("Negative count");
            exceptionService.throwBadRequest();
        }
        log.info("Поступил запрос на получение списка топ {} популярных фильмов", count);
        return filmService.getTop10Films(count);
    }
}
