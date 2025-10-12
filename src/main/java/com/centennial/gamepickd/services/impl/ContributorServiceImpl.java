package com.centennial.gamepickd.services.impl;

import com.centennial.gamepickd.dtos.AddContributorDTO;
import com.centennial.gamepickd.entities.AuthorityType;
import com.centennial.gamepickd.entities.User;
import com.centennial.gamepickd.repository.contracts.AuthorityTypeDAO;
import com.centennial.gamepickd.repository.contracts.ContributorDAO;
import com.centennial.gamepickd.repository.contracts.UserDAO;
import com.centennial.gamepickd.services.contracts.ContributorService;
import com.centennial.gamepickd.util.Exceptions;
import com.centennial.gamepickd.util.Mapper;
import com.centennial.gamepickd.util.enums.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContributorServiceImpl implements ContributorService {

    private final ContributorDAO contributorDAO;
    private final UserDAO userDAO;
    private final AuthorityTypeDAO authorityTypeDAO;
    private final PasswordEncoder passwordEncoder;
    private final Mapper mapper;

    @Autowired
    public ContributorServiceImpl(
            @Qualifier("contributorDAOJpaImpl") ContributorDAO contributorDAO,
            @Qualifier("userDAOJpaImpl") UserDAO userDAO,
            @Qualifier("authorityTypeDAOJpaImpl") AuthorityTypeDAO authorityTypeDAO,
            PasswordEncoder passwordEncoder,
            Mapper mapper
    ) {
        this.contributorDAO = contributorDAO;
        this.userDAO = userDAO;
        this.authorityTypeDAO = authorityTypeDAO;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    @Transactional
    @Override
    public void add(AddContributorDTO dto) throws Exceptions.UsernameAlreadyExistsException, Exceptions.EmailAlreadyExistsException {
        List<User> existingUsers = userDAO.findByUsernameOrEmail(dto.username(), dto.email());

        for (User existing : existingUsers) {
            if (existing.getUsername().equals(dto.username())) {
                throw new Exceptions.UsernameAlreadyExistsException("Username already exists: " + dto.username());
            }
            if (existing.getEmail().equals(dto.email())) {
                throw new Exceptions.EmailAlreadyExistsException("Email already exists: " + dto.email());
            }
        }

        AuthorityType memberAuthority = authorityTypeDAO.findByLabel(RoleType.MEMBER)
                .orElseThrow(() -> new IllegalStateException("Role MEMBER not found in DB"));

        User user = mapper.AddContributorDtoToUser(dto, memberAuthority, passwordEncoder);
        userDAO.create(user);
        contributorDAO.create(mapper.AddContributorDtoToContributor(dto, user));
    }
}
