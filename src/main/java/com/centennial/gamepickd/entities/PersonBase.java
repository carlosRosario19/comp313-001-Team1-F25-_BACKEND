package com.centennial.gamepickd.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NullMarked;

@NullMarked
@MappedSuperclass
public abstract class PersonBase extends Auditable {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    private User user;

    @NotBlank(message = "First name cannot be blank")
    @Column(name = "FIRST_NAME")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Column(name = "LAST_NAME")
    private String lastName;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

