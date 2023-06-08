package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.validator.AfterDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Validated
public class Film {

    private Set<Long> likes = new HashSet<>();
    private Long id;
    @NotBlank(message = "Title is null or blank.")
    private String name;
    @Size(max = 200, message = "Description length is more than 200 characters.")
    private String description;
    @AfterDate("28-12-1895")
    private LocalDate releaseDate;
    @Positive(message = "Duration is negative.")
    private Integer duration;

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Set<Long> getLikes() {
        return likes;
    }
}
