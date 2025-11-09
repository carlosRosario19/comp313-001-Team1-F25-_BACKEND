package com.centennial.gamepickd.services.impl;

import com.centennial.gamepickd.dtos.UserDTO;
import com.centennial.gamepickd.repository.contracts.UserDAO;
import com.centennial.gamepickd.services.contracts.UserService;
import com.centennial.gamepickd.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final Mapper mapper;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, Mapper mapper) {
        this.userDAO = userDAO;
        this.mapper = mapper;
    }

    @Override
    public Set<UserDTO> getAll() {
        return userDAO.findAll().stream()
                .map(mapper::userToUserDto)
                .collect(Collectors.toSet());
    }
}
