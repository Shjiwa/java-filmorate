package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new LinkedHashMap<>();

    @GetMapping
    public List<Film> getFilms() {
        log.info("Количество фильмов в текущий момент: " + films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            log.error("Данный фильм уже существует.");
            throw new ValidationException("Данный фильм уже существует.");
        }
        film.setId(films.size() + 1);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {} с id {}", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Данного фильма не существует.");
            throw new ValidationException("Данного фильма не существует.");
        }
        films.put(film.getId(), film);
        log.info("Обновлен фильм: {} с id {}", film.getName(), film.getId());
        return film;
    }
}
