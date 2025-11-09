package com.centennial.gamepickd.repository.contracts;

import com.centennial.gamepickd.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserDAO {
    void create(User user);
    Optional<User> findByUsername(String username);
    List<User> findByUsernameOrEmail(String username, String email);
    Set<User> findAll();
}
