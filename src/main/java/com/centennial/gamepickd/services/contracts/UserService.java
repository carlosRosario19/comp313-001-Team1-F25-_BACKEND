package com.centennial.gamepickd.services.contracts;

import com.centennial.gamepickd.dtos.UserDTO;

import java.util.Set;

public interface UserService {

    Set<UserDTO> getAll();
}
