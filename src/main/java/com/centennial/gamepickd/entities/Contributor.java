package com.centennial.gamepickd.entities;

import jakarta.persistence.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
@Entity
@Table(name = "CONTRIBUTORS")
public class Contributor extends PersonBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONTRIBUTOR_ID")
    private long id;

    @OneToMany(mappedBy = "contributor", cascade = CascadeType.ALL, orphanRemoval = true)
    private @Nullable List<Game> games = new ArrayList<>();

    public Contributor() {}

    public static class Builder {
        // Required parameters
        private long id;
        private User user;
        private String firstName;
        private String lastName;
        private List<Game> games;

        public Builder id(long val) {
            id = val;
            return this;
        }
        public Builder user(User val) {
            user = val;
            return this;
        }
        public Builder firstName(String val) {
            firstName = val;
            return this;
        }
        public Builder lastName(String val) {
            lastName = val;
            return this;
        }
        public Builder games(List<Game> val) {
            games = val;
            return this;
        }
        public Contributor build() {
            return new Contributor(this);
        }
    }

    private Contributor(Builder builder) {
        id = builder.id;
        super.setUser(builder.user);
        super.setFirstName(builder.firstName);
        super.setLastName(builder.lastName);
        games = builder.games;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public @Nullable List<Game> getGames() {
        return games;
    }

    public void setGames(@Nullable List<Game> games) {
        this.games = games;
    }

    @Override
    public String toString() {
        String gameTitles = (games != null && !games.isEmpty())
                ? games.stream().map(Game::getTitle).toList().toString()
                : "[]";

        return "Contributor{" +
                "id=" + id +
                ", user='" + super.getUser() + '\'' +
                ", firstName='" + super.getFirstName() + '\'' +
                ", lastName='" + super.getLastName() + '\'' +
                ", games=" + gameTitles +
                '}';
    }


}
