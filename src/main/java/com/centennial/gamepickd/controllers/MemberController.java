package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.dtos.AddMemberDTO;
import com.centennial.gamepickd.services.contracts.MemberService;
import com.centennial.gamepickd.util.Exceptions;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(@Qualifier("memberServiceImpl") MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("members")
    public void addMember(@Valid @RequestBody AddMemberDTO addMemberDTO) throws
            Exceptions.UsernameAlreadyExistsException,
            Exceptions.EmailAlreadyExistsException
    {
        memberService.add(addMemberDTO);
    }
}
