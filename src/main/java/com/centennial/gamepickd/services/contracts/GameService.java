package com.centennial.gamepickd.services.contracts;

import com.centennial.gamepickd.dtos.AddGameDTO;
import com.centennial.gamepickd.util.Exceptions;

public interface GameService {

    void add(AddGameDTO addGameDTO) throws Exceptions.GameAlreadyExistsException, Exceptions.StorageException, Exceptions.ContributorNotFoundException;
}
