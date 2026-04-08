package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder(toBuilder = true)
public class FilmDto {

    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private RatingDto mpa;

    private int duration;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    @Builder.Default
    private List<GenreDto> genres = new ArrayList<>();

    public void setLikes(Collection<Long> likes) {
        this.likes = new HashSet<>(likes);
    }

    public void setGenres(Collection<GenreDto> genres) {
        for (GenreDto genre : genres) {
            if (!this.genres.contains(genre)) {
                this.genres.add(genre);
            }
        }
    }
}
