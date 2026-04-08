package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
public class Film {

    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    @Builder.Default
    private Long ratingId = null;

    @Builder.Default
    private String ratingName = null;

    private int duration;
}
