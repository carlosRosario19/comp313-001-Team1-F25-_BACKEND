package com.centennial.gamepickd.entities;

import com.centennial.gamepickd.util.enums.GenreType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
@Entity
@Table(name = "GENRES")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GENRE_ID")
    private long id;

    @NotNull
    @Column(name = "LABEL")
    private GenreType label;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "GAMES_GENRES",
            joinColumns = @JoinColumn(name = "GENRE_ID"),
            inverseJoinColumns = @JoinColumn(name = "GAME_ID"))
    private List<Game> games = new ArrayList<>();

    public Genre(){}

    public Genre(GenreType label, List<Game> games) {
        this.label = label;
        this.games = games;
    }

    public Genre(GenreType label) {
        this.label = label;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GenreType getLabel() {
        return label;
    }

    public void setLabel(GenreType label) {
        this.label = label;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> books) {
        this.games = books;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "label='" + label.getVal() + '\'' +
                ", games=" + games +
                '}';
    }
}
