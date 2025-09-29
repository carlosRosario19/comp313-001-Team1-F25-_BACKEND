package com.centennial.gamepickd.util;

import com.centennial.gamepickd.dtos.AddMemberDTO;
import com.centennial.gamepickd.entities.AuthorityType;
import com.centennial.gamepickd.entities.Member;
import com.centennial.gamepickd.entities.User;
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

    public Member AddMemberDtoToMember(AddMemberDTO dto, User user) {
        return new Member.Builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .user(user)
                .build();
    }
}
