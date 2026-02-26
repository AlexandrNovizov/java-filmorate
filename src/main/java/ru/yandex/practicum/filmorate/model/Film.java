package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "likes")
@Builder(toBuilder = true)
public class Film {

    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private int duration;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();
}
