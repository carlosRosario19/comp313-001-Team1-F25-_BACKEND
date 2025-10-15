package com.centennial.gamepickd.entities;

import jakarta.persistence.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
@Entity
@Table(name = "GAMES")
public class Game extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GAME_ID")
    private long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "COVER_IMAGE_PATH")
    private String coverImagePath;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "GAMES_GENRES",
            joinColumns = @JoinColumn(name = "GAME_ID"),
            inverseJoinColumns = @JoinColumn(name = "GENRE_ID"))
    private @Nullable List<Genre> genres;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTRIBUTOR_ID", nullable = false)
    private @Nullable Contributor contributor;

    public Game(){}

    public Game(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Game(String title, String description, String coverImagePath, @Nullable List<Genre> genres, @Nullable Contributor contributor) {
        this.title = title;
        this.description = description;
        this.coverImagePath = coverImagePath;
        this.genres = genres;
        this.contributor = contributor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }

    public @Nullable List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(@Nullable List<Genre> genres) {
        this.genres = genres;
    }

    public @Nullable Contributor getContributor() {
        return contributor;
    }

    public void setContributor(@Nullable Contributor contributor) {
        this.contributor = contributor;
    }

    public void addGenre(Genre genre){
        if(this.genres == null){
            this.genres = new ArrayList<>();
        }
        this.genres.add(genre);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", coverImagePath='" + coverImagePath + '\'' +
                ", genres=" + genres +
                ", contributor=" + contributor +
                '}';
    }
}
