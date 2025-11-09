package com.centennial.gamepickd.entities;

import com.centennial.gamepickd.util.enums.PublisherType;
import jakarta.persistence.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
@Entity
@Table(name = "PUBLISHERS")
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PUBLISHER_ID")
    private long id;

    @Column(name = "NAME")
    private PublisherType name;

    @OneToMany(
            mappedBy = "publisher",
            cascade = {CascadeType.DETACH, CascadeType.REFRESH},
            fetch = FetchType.LAZY
    )
    private @Nullable List<Game> games = new ArrayList<>();

    public Publisher() {}

    public Publisher(PublisherType name, @Nullable List<Game> games) {
        this.name = name;
        this.games = games;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PublisherType getName() {
        return name;
    }

    public void setName(PublisherType name) {
        this.name = name;
    }

    public @Nullable List<Game> getGames() {
        return games;
    }

    public void setGames(@Nullable List<Game> games) {
        this.games = games;
    }

    @Override
    public String toString() {
        return "Publisher{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
