package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.dtos.AddMemberDTO;
import com.centennial.gamepickd.services.contracts.MemberService;
import com.centennial.gamepickd.util.Exceptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    private AddMemberDTO validMemberDTO;

    @BeforeEach
    void setup() {
        validMemberDTO = new AddMemberDTO(
                "carlos",
                "rosario",
                "test19@gmail.com",
                "carlos19",
                "test123"
        );
    }

    @Test
    void saveMember_shouldReturnOk_whenMemberIsSaved() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMemberDTO)))
                .andExpect(status().isOk());

        verify(memberService).add(validMemberDTO);
    }

    @Test
    void saveMember_shouldReturnConflict_whenUsernameAlreadyExists() throws Exception{
        Mockito.doThrow(new Exceptions.UsernameAlreadyExistsException("Username already exists in the database"))
                .when(memberService).add(Mockito.any(AddMemberDTO.class));

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMemberDTO)))
                .andExpect(status().isConflict());

        verify(memberService).add(validMemberDTO);
    }

    @Test
    void saveMember_shouldReturnConflict_whenEmailAlreadyExists() throws Exception{
        Mockito.doThrow(new Exceptions.EmailAlreadyExistsException("Email already exists in the database"))
                .when(memberService).add(Mockito.any(AddMemberDTO.class));

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMemberDTO)))
                .andExpect(status().isConflict());

        verify(memberService).add(validMemberDTO);
    }
}
