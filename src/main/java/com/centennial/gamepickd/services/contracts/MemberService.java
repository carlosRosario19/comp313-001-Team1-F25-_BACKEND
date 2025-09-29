package com.centennial.gamepickd.services.contracts;

import com.centennial.gamepickd.dtos.AddMemberDTO;
import com.centennial.gamepickd.util.Exceptions;

public interface MemberService {
    void add(AddMemberDTO member) throws
            Exceptions.UsernameAlreadyExistsException,
            Exceptions.EmailAlreadyExistsException;
}
