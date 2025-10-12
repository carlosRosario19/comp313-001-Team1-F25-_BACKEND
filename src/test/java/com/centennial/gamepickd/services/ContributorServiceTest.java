package com.centennial.gamepickd.services;

import com.centennial.gamepickd.dtos.AddContributorDTO;
import com.centennial.gamepickd.entities.AuthorityType;
import com.centennial.gamepickd.entities.Contributor;
import com.centennial.gamepickd.entities.Member;
import com.centennial.gamepickd.entities.User;
import com.centennial.gamepickd.repository.contracts.AuthorityTypeDAO;
import com.centennial.gamepickd.repository.contracts.ContributorDAO;
import com.centennial.gamepickd.repository.contracts.UserDAO;
import com.centennial.gamepickd.services.contracts.ContributorService;
import com.centennial.gamepickd.services.impl.ContributorServiceImpl;
import com.centennial.gamepickd.util.Exceptions;
import com.centennial.gamepickd.util.Mapper;
import com.centennial.gamepickd.util.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

class ContributorServiceTest {

    @Mock
    private ContributorDAO contributorDAO;

    @Mock
    private UserDAO userDAO;

    @Mock
    private AuthorityTypeDAO authorityTypeDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ContributorServiceImpl contributorService;

    private AddContributorDTO validContributorDTO;

    private Mapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new Mapper();
        contributorService = new ContributorServiceImpl(contributorDAO, userDAO, authorityTypeDAO, passwordEncoder, mapper);


        validContributorDTO = new AddContributorDTO(
                "123123987",
                "Peter",
                "Zeus",
                "6474259589",
                "test123"
        );
    }

    @Test
    void add_ShouldCreateUserAndMember_WhenValid() throws Exception {
        // Arrange: no existing users, role MEMBER exists
        when(userDAO.findByUsernameOrEmail(validContributorDTO.username(), validContributorDTO.email()))
                .thenReturn(List.of());

        AuthorityType memberAuthority = new AuthorityType();
        memberAuthority.setLabel(RoleType.MEMBER);

        when(authorityTypeDAO.findByLabel(RoleType.MEMBER))
                .thenReturn(Optional.of(memberAuthority));

        when(passwordEncoder.encode("test123")).thenReturn("hashedPassword");

        // Act
        contributorService.add(validContributorDTO);

        // Assert
        verify(userDAO).create(any(User.class));
        verify(contributorDAO).create(any(Contributor.class));
    }

    @Test
    void add_ShouldThrow_WhenUsernameAlreadyExists() {
        // Arrange: existing user with same username
        User existingUser = new User();
        existingUser.setUsername(validContributorDTO.username());
        existingUser.setEmail("different@test.com");

        when(userDAO.findByUsernameOrEmail(validContributorDTO.username(), validContributorDTO.email()))
                .thenReturn(List.of(existingUser));

        // Act + Assert
        assertThrows(Exceptions.UsernameAlreadyExistsException.class,
                () -> contributorService.add(validContributorDTO));

        verify(userDAO, never()).create(any());
        verify(contributorDAO, never()).create(any());
    }

    @Test
    void add_ShouldThrow_WhenEmailAlreadyExists() {
        // Arrange: existing user with same email
        User existingUser = new User();
        existingUser.setUsername("differentUser");
        existingUser.setEmail(validContributorDTO.email());

        when(userDAO.findByUsernameOrEmail(validContributorDTO.username(), validContributorDTO.email()))
                .thenReturn(List.of(existingUser));

        // Act + Assert
        assertThrows(Exceptions.EmailAlreadyExistsException.class,
                () -> contributorService.add(validContributorDTO));

        verify(userDAO, never()).create(any());
        verify(contributorDAO, never()).create(any());
    }

    @Test
    void add_ShouldThrow_WhenRoleMemberNotFound() {
        // Arrange: no existing users, but no MEMBER role in DB
        when(userDAO.findByUsernameOrEmail(validContributorDTO.username(), validContributorDTO.email()))
                .thenReturn(List.of());

        when(authorityTypeDAO.findByLabel(RoleType.MEMBER))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(IllegalStateException.class,
                () -> contributorService.add(validContributorDTO));

        verify(userDAO, never()).create(any());
        verify(contributorDAO, never()).create(any());
    }
}
