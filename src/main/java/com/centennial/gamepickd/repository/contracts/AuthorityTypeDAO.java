package com.centennial.gamepickd.repository.contracts;

import com.centennial.gamepickd.entities.AuthorityType;
import com.centennial.gamepickd.util.enums.RoleType;

import java.util.Optional;

public interface AuthorityTypeDAO {
    Optional<AuthorityType> findByLabel(RoleType label);
}
