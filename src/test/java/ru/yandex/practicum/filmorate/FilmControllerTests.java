package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTests {

    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void shouldNotValidateNullFilm() {
        Film film = new Film();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(2, violations.size(), "Film is valid.");
    }

    @Test
    void shouldValidateCorrectFilm() {
        Film film = new Film();
        film.setName("Avatar");
        film.setDescription("Avatar description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size(), "Film is not valid.");
    }

    @Test
    void shouldNotValidateNullTitle() {
        Film film = new Film();
        film.setName(null);
        film.setDescription("Avatar description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Title is not null.");
    }

    @Test
    void shouldNotValidateBlankTitle() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Avatar description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Title is not blank.");
    }

    @Test
    void shouldNotValidateDescriptionLengthMoreThan200Characters() {
        Film film = new Film();
        film.setName("Avatar");
        film.setDescription("a".repeat(202));
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Description length are less than 200 characters.");
    }

    @Test
    void shouldNotValidateReleaseDateBefore28December1895() {
        Film film = new Film();
        film.setName("Avatar");
        film.setDescription("Avatar description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Release date is valid.");
    }

    @Test
    void shouldNotValidateNegativeDuration() {
        Film film = new Film();
        film.setName("Avatar");
        film.setDescription("Avatar description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(-120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Duration is valid.");
    }
}