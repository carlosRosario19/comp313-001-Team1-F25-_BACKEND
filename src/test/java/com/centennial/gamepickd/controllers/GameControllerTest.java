package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.dtos.AddGameDTO;
import com.centennial.gamepickd.services.contracts.GameService;
import com.centennial.gamepickd.services.contracts.StorageService;
import com.centennial.gamepickd.util.Exceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    @MockitoBean
    private StorageService storageService;

    private AddGameDTO validGameDTO;

    private MockMultipartFile coverImage;

    @BeforeEach
    void setup() {
        coverImage = new MockMultipartFile(
                "coverImage",
                "cover.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy-image-data".getBytes()
        );
        validGameDTO = new AddGameDTO(
                "test game title",
                "test game description",
                "RPG,MMORPG",
                "contributor",
                coverImage
        );
    }

    @Test
    @WithMockUser(username = "contributor", roles = {"CONTRIBUTOR"})
    void saveGame_shouldReturnOk_whenGameIsSaved() throws Exception {
        mockMvc.perform(
                multipart("/api/games")
                        .file(coverImage)
                        .param("title", validGameDTO.title())
                        .param("description", validGameDTO.description())
                        .param("genres", validGameDTO.genres())
                        .param("contributorUsername", validGameDTO.contributorUsername())
        ).andExpect(status().isOk());

        verify(gameService).add(any(AddGameDTO.class));
    }

    @Test
    @WithMockUser(username = "contributor", roles = {"CONTRIBUTOR"})
    void saveGame_shouldReturnConflict_whenGameAlreadyExists() throws Exception {
        doThrow(new Exceptions.GameAlreadyExistsException("Game already exists"))
                .when(gameService).add(any(AddGameDTO.class));

        mockMvc.perform(
                multipart("/api/games")
                        .file(coverImage)
                        .param("title", validGameDTO.title())
                        .param("description", validGameDTO.description())
                        .param("genres", validGameDTO.genres())
                        .param("contributorUsername", validGameDTO.contributorUsername())
        ).andExpect(status().isConflict()); // 409 if you map that exception
    }

    @Test
    @WithMockUser(username = "contributor", roles = {"CONTRIBUTOR"})
    void saveGame_shouldReturnNotFound_whenContributorDoesNotExist() throws Exception {
        doThrow(new Exceptions.ContributorNotFoundException("Contributor not found"))
                .when(gameService).add(any(AddGameDTO.class));

        mockMvc.perform(
                multipart("/api/games")
                        .file(coverImage)
                        .param("title", validGameDTO.title())
                        .param("description", validGameDTO.description())
                        .param("genres", validGameDTO.genres())
                        .param("contributorUsername", validGameDTO.contributorUsername())
        ).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "contributor", roles = {"CONTRIBUTOR"})
    void saveGame_shouldReturnInternalServerError_whenStorageFails() throws Exception {
        doThrow(new Exceptions.StorageException("Storage failed"))
                .when(gameService).add(any(AddGameDTO.class));

        mockMvc.perform(
                multipart("/api/games")
                        .file(coverImage)
                        .param("title", validGameDTO.title())
                        .param("description", validGameDTO.description())
                        .param("genres", validGameDTO.genres())
                        .param("contributorUsername", validGameDTO.contributorUsername())
        ).andExpect(status().isBadRequest());
    }

}
