package ru.yandex.practicum.filmorate.servise;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

// Сначала делал через этот валидатор, но в процессе перешел на валидацию через аннотации
// Решил не удалять и оставить пока что пылиться тут, вдруг пригодится еще
@Slf4j
public class ValidateService {
    void validateUser(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Email is blank or not contains symbol: @");
            throw new ValidationException("Email is blank or not contains symbol: @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Login is blank or contains whitespaces.");
            throw new ValidationException("Login is blank or contains whitespaces.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Birthday in future.");
            throw new ValidationException("Birthday in future.");
        }
    }

    void validateFilm(Film film) {
        if (film.getName().isBlank()) {
            log.error("Title is blank.");
            throw new ValidationException("Title is blank.");
        }
        if (film.getDescription().length() > 200) {
            log.error("Description length is more than 200 characters.");
            throw new ValidationException("Description length is more than 200 characters.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Release date is before December 28, 1895.");
            throw new ValidationException("Release date is before December 28, 1895.");
        }
        if (film.getDuration() < 1) {
            log.error("Duration is negative");
            throw new ValidationException("Duration is negative");
        }
    }
}
