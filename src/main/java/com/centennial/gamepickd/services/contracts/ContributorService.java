package com.centennial.gamepickd.services.contracts;

import com.centennial.gamepickd.dtos.AddContributorDTO;
import com.centennial.gamepickd.util.Exceptions;

public interface ContributorService {
    void add(AddContributorDTO addContributorDTO) throws Exceptions.UsernameAlreadyExistsException, Exceptions.EmailAlreadyExistsException;
}
