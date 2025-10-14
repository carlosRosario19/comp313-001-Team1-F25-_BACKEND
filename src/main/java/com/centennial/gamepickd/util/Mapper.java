package com.centennial.gamepickd.util;

import com.centennial.gamepickd.dtos.AddContributorDTO;
import com.centennial.gamepickd.dtos.AddGameDTO;
import com.centennial.gamepickd.dtos.AddMemberDTO;
import com.centennial.gamepickd.entities.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Mapper {

    public User AddMemberDtoToUser(AddMemberDTO dto, AuthorityType authorityType, PasswordEncoder passwordEncoder){
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        String hashedPassword = passwordEncoder.encode(dto.password());
        user.setPassword(hashedPassword);
        user.setEnabled(true);
        Set<AuthorityType> authorities = Set.of(authorityType);
        user.setAuthorities(authorities);
        return user;
    }

    public User AddContributorDtoToUser(AddContributorDTO dto, AuthorityType authorityType, PasswordEncoder passwordEncoder){
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        String hashedPassword = passwordEncoder.encode(dto.password());
        user.setPassword(hashedPassword);
        user.setEnabled(true);
        Set<AuthorityType> authorities = Set.of(authorityType);
        user.setAuthorities(authorities);
        return user;
    }

    public Member AddMemberDtoToMember(AddMemberDTO dto, User user) {
        return new Member.Builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .user(user)
                .build();
    }

    public Contributor AddContributorDtoToContributor(AddContributorDTO dto, User user) {
        return new Contributor.Builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .user(user)
                .build();
    }

    public Game addGameDtoToGame(AddGameDTO addGameDTO) {
        return new Game(addGameDTO.title(), addGameDTO.description());
    }
}
