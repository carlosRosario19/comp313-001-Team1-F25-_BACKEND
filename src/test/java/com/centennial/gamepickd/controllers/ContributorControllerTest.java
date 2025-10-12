package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.dtos.AddContributorDTO;
import com.centennial.gamepickd.services.contracts.ContributorService;
import com.centennial.gamepickd.util.Exceptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ContributorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContributorService contributorService;

    @Autowired
    private ObjectMapper objectMapper;

    private AddContributorDTO validAddContributorDTO;

    @BeforeEach
    void setup() {
        validAddContributorDTO = new AddContributorDTO(
                "carlos",
                "rosario",
                "test19@gmail.com",
                "carlos19",
                "test123"
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveContributor_shouldReturnOk_whenContributorIsSaved() throws Exception {
        mockMvc.perform(post("/api/contributors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAddContributorDTO)))
                .andExpect(status().isOk());

        verify(contributorService).add(validAddContributorDTO);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveMember_shouldReturnConflict_whenUsernameAlreadyExists() throws Exception{
        Mockito.doThrow(new Exceptions.UsernameAlreadyExistsException("Username already exists in the database"))
                .when(contributorService).add(Mockito.any(AddContributorDTO.class));

        mockMvc.perform(post("/api/contributors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAddContributorDTO)))
                .andExpect(status().isConflict());

        verify(contributorService).add(validAddContributorDTO);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveMember_shouldReturnConflict_whenEmailAlreadyExists() throws Exception{
        Mockito.doThrow(new Exceptions.EmailAlreadyExistsException("Email already exists in the database"))
                .when(contributorService).add(Mockito.any(AddContributorDTO.class));

        mockMvc.perform(post("/api/contributors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAddContributorDTO)))
                .andExpect(status().isConflict());

        verify(contributorService).add(validAddContributorDTO);
    }

    @Test
    void saveContributor_shouldUnauthorized_whenNoAuthenticated() throws Exception {
        mockMvc.perform(post("/api/contributors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAddContributorDTO)))
                .andExpect(status().isUnauthorized());
    }


}
