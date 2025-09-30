package com.centennial.gamepickd.entities;

import jakarta.persistence.*;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Entity
@Table(name = "MEMBERS")
public class Member extends PersonBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private long id;

    public Member() {}

    public static class Builder {
        // Required parameters
        private long id;
        private User user;
        private String firstName;
        private String lastName;

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
        public Member build() {
            return new Member(this);
        }
    }

    private Member(Builder builder) {
        id = builder.id;
        super.setUser(builder.user);
        super.setFirstName(builder.firstName);
        super.setLastName(builder.lastName);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", user='" + super.getUser() + '\'' +
                ", firstName='" + super.getFirstName() + '\'' +
                ", lastName='" + super.getLastName() + '\'' +
                '}';
    }
}
