package com.centennial.gamepickd.entities;

import com.centennial.gamepickd.util.enums.PlatformType;
import jakarta.persistence.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
@Entity
@Table(name = "PLATFORMS")
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PLATFORM_ID")
    private long id;

    @Column(name = "NAME")
    private PlatformType name;

    @ManyToMany(mappedBy = "platforms", fetch = FetchType.LAZY)
    private @Nullable List<Game> games = new ArrayList<>();

    public Platform() {}

    public Platform(PlatformType name, @Nullable List<Game> games) {
        this.name = name;
        this.games = games;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PlatformType getName() {
        return name;
    }

    public void setName(PlatformType name) {
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
        return "Platform{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
