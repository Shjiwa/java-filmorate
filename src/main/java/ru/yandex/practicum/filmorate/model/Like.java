package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class Like {

    @NotNull
    private final Long id;

    @NotNull
    private final Long filmId;

    @NotNull
    private final Long userId;
}
