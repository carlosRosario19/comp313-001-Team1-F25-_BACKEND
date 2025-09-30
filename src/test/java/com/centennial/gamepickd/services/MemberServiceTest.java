package com.centennial.gamepickd.services;

import com.centennial.gamepickd.dtos.AddMemberDTO;
import com.centennial.gamepickd.entities.AuthorityType;
import com.centennial.gamepickd.entities.Member;
import com.centennial.gamepickd.entities.User;
import com.centennial.gamepickd.repository.contracts.AuthorityTypeDAO;
import com.centennial.gamepickd.repository.contracts.MemberDAO;
import com.centennial.gamepickd.repository.contracts.UserDAO;
import com.centennial.gamepickd.services.impl.MemberServiceImpl;
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

class MemberServiceTest {

    @Mock
    private MemberDAO memberDAO;

    @Mock
    private UserDAO userDAO;

    @Mock
    private AuthorityTypeDAO authorityTypeDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberServiceImpl memberService;

    private AddMemberDTO validMemberDTO;

    private Mapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new Mapper();
        memberService = new MemberServiceImpl(memberDAO, userDAO, authorityTypeDAO, passwordEncoder, mapper);


        validMemberDTO = new AddMemberDTO(
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
        when(userDAO.findByUsernameOrEmail(validMemberDTO.username(), validMemberDTO.email()))
                .thenReturn(List.of());

        AuthorityType memberAuthority = new AuthorityType();
        memberAuthority.setLabel(RoleType.MEMBER);

        when(authorityTypeDAO.findByLabel(RoleType.MEMBER))
                .thenReturn(Optional.of(memberAuthority));

        when(passwordEncoder.encode("test123")).thenReturn("hashedPassword");

        // Act
        memberService.add(validMemberDTO);

        // Assert
        verify(userDAO).create(any(User.class));
        verify(memberDAO).create(any(Member.class));
    }

    @Test
    void add_ShouldThrow_WhenUsernameAlreadyExists() {
        // Arrange: existing user with same username
        User existingUser = new User();
        existingUser.setUsername(validMemberDTO.username());
        existingUser.setEmail("different@test.com");

        when(userDAO.findByUsernameOrEmail(validMemberDTO.username(), validMemberDTO.email()))
                .thenReturn(List.of(existingUser));

        // Act + Assert
        assertThrows(Exceptions.UsernameAlreadyExistsException.class,
                () -> memberService.add(validMemberDTO));

        verify(userDAO, never()).create(any());
        verify(memberDAO, never()).create(any());
    }

    @Test
    void add_ShouldThrow_WhenEmailAlreadyExists() {
        // Arrange: existing user with same email
        User existingUser = new User();
        existingUser.setUsername("differentUser");
        existingUser.setEmail(validMemberDTO.email());

        when(userDAO.findByUsernameOrEmail(validMemberDTO.username(), validMemberDTO.email()))
                .thenReturn(List.of(existingUser));

        // Act + Assert
        assertThrows(Exceptions.EmailAlreadyExistsException.class,
                () -> memberService.add(validMemberDTO));

        verify(userDAO, never()).create(any());
        verify(memberDAO, never()).create(any());
    }

    @Test
    void add_ShouldThrow_WhenRoleMemberNotFound() {
        // Arrange: no existing users, but no MEMBER role in DB
        when(userDAO.findByUsernameOrEmail(validMemberDTO.username(), validMemberDTO.email()))
                .thenReturn(List.of());

        when(authorityTypeDAO.findByLabel(RoleType.MEMBER))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(IllegalStateException.class,
                () -> memberService.add(validMemberDTO));

        verify(userDAO, never()).create(any());
        verify(memberDAO, never()).create(any());
    }

}
