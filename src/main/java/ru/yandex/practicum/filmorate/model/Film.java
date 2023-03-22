package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.validator.AfterDate;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@Builder
@Validated
public class Film {
    private int id;

    @NotBlank(message = "Title is null or blank.")
    private String name;

    @Size(max = 200, message = "Description length is more than 200 characters.")
    private String description;

    @AfterDate("28-12-1895")
    private LocalDate releaseDate;

    @Positive(message = "Duration is negative.")
    private Integer duration;

    public Film(int id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
