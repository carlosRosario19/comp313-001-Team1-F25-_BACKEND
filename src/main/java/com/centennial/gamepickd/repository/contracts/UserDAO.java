package com.centennial.gamepickd.repository.contracts;

import com.centennial.gamepickd.entities.User;

import java.util.Optional;

public interface UserDAO {
    void create(User user);
    Optional<User> findByUsername(String username);
}
