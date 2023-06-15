package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.validator.AfterDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Validated
public class Film {

    private Set<Long> likes = new HashSet<>();

    private Set<Genre> genres = new HashSet<>();

    private Long id;

    @NotBlank(message = "Title is null or blank.")
    private String name;

    @Size(max = 200, message = "Description length is more than 200 characters.")
    private String description;

    @AfterDate("28-12-1895")
    private LocalDate releaseDate;

    @Positive(message = "Duration is negative.")
    private Integer duration;

    private Integer rating;

    private Mpa mpa;
}
