package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Builder
@Data
public class Genre implements Comparable<Genre> {

    @NotNull
    private int id;
    @NotNull
    private String name;

    @Override
    public int compareTo(Genre o) {
        return id - o.getId();
    }
}
