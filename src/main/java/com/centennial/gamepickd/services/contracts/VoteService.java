package com.centennial.gamepickd.services.contracts;

import com.centennial.gamepickd.dtos.AddVoteDTO;
import com.centennial.gamepickd.util.Exceptions;

public interface VoteService {
    void add(AddVoteDTO addVoteDTO) throws Exceptions.ReviewNotFoundException;
}
