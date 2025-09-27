package com.centennial.gamepickd.entities;

import com.centennial.gamepickd.util.converters.CharToBooleanConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;

@NullMarked
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private long id;

    @NotBlank(message = "Username cannot be blank")
    @Column(name = "USERNAME")
    private String username;

    @NotNull
    @Email
    @Column(name = "EMAIL")
    private String email;

    @NotNull
    @Size(min = 5, message = "Password must be at least 5 characters long")
    @Column(name = "PASSWORD")
    private String password;

    @NotNull
    @Column(name = "ENABLED", nullable = false)
    @Convert(converter = CharToBooleanConverter.class)
    private Boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USERS_AUTHORITIES",
            joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "AUTHORITY_TYPE_ID", referencedColumnName = "AUTHORITY_TYPE_ID")
    )
    private Set<AuthorityType> authorities = new HashSet<>();

    public User() {}

    public User(long id, String username, String email, String password, Boolean enabled, Set<AuthorityType> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Set<AuthorityType> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<AuthorityType> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", authorities=" + authorities +
                '}';
    }
}
