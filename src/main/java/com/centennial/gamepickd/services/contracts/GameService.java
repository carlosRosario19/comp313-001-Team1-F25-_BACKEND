package com.centennial.gamepickd.services.contracts;

import com.centennial.gamepickd.dtos.AddGameDTO;
import com.centennial.gamepickd.dtos.GameDTO;
import com.centennial.gamepickd.dtos.SearchGameDTO;
import com.centennial.gamepickd.util.Exceptions;
import org.springframework.data.domain.Page;

public interface GameService {

    void add(AddGameDTO addGameDTO) throws Exceptions.GameAlreadyExistsException, Exceptions.StorageException, Exceptions.ContributorNotFoundException;
    Page<GameDTO> getAll(SearchGameDTO searchGameDTO) throws Exceptions.PageOutOfRangeException;
}
