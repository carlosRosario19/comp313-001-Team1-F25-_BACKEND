package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.dtos.AddGameDTO;
import com.centennial.gamepickd.dtos.GameDTO;
import com.centennial.gamepickd.services.contracts.GameService;
import com.centennial.gamepickd.services.contracts.StorageService;
import com.centennial.gamepickd.util.Exceptions;
import com.centennial.gamepickd.util.enums.GenreType;
import com.centennial.gamepickd.util.enums.PlatformType;
import com.centennial.gamepickd.util.enums.PublisherType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                "UBISOFT",
                "PlayStation 5,PC (Windows)",
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
                        .param("publisher", validGameDTO.publisher())
                        .param("platforms", validGameDTO.platforms())
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
                        .param("publisher", validGameDTO.publisher())
                        .param("platforms", validGameDTO.platforms())
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
                        .param("publisher", validGameDTO.publisher())
                        .param("platforms", validGameDTO.platforms())
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
                        .param("publisher", validGameDTO.publisher())
                        .param("platforms", validGameDTO.platforms())
                        .param("contributorUsername", validGameDTO.contributorUsername())
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "contributor", roles = {"CONTRIBUTOR"})
    void saveGame_shouldReturnNotFound_whenPublisherDoesNotExist() throws Exception {
        doThrow(new Exceptions.PublisherNotFoundException("Publisher not found"))
                .when(gameService).add(any(AddGameDTO.class));

        mockMvc.perform(
                multipart("/api/games")
                        .file(coverImage)
                        .param("title", validGameDTO.title())
                        .param("description", validGameDTO.description())
                        .param("genres", validGameDTO.genres())
                        .param("publisher", validGameDTO.publisher())
                        .param("platforms", validGameDTO.platforms())
                        .param("contributorUsername", validGameDTO.contributorUsername())
        ).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "contributor", roles = {"CONTRIBUTOR"})
    void saveGame_shouldReturnNotFound_whenPlatformDoesNotExist() throws Exception {
        doThrow(new Exceptions.PlatformNotFoundException("Platform not found"))
                .when(gameService).add(any(AddGameDTO.class));

        mockMvc.perform(
                multipart("/api/games")
                        .file(coverImage)
                        .param("title", validGameDTO.title())
                        .param("description", validGameDTO.description())
                        .param("genres", validGameDTO.genres())
                        .param("publisher", validGameDTO.publisher())
                        .param("platforms", validGameDTO.platforms())
                        .param("contributorUsername", validGameDTO.contributorUsername())
        ).andExpect(status().isNotFound());
    }

    @Test
    void getAllGames_shouldReturnOkAndPagedResults() throws Exception {
        // Arrange
        GameDTO sampleGame = new GameDTO(
                1L,
                "Sample Game",
                "A cool adventure game",
                "cover.png",
                Set.of(GenreType.RPG),
                PublisherType.EPIC_GAMES,
                Set.of(PlatformType.PC_WINDOWS),
                LocalDateTime.now()
        );

        Page<GameDTO> mockPage = new PageImpl<>(List.of(sampleGame), PageRequest.of(0, 12), 1);
        when(gameService.getAll(any())).thenReturn(mockPage);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/games")
                        .param("page", "0")
                        .param("size", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.gameDTOList[0].title").value("Sample Game"))
                .andExpect(jsonPath("$._embedded.gameDTOList[0].publisher").value("EPIC_GAMES"))
                .andExpect(jsonPath("$._embedded.gameDTOList[0].genres[0]").value("RPG"));
    }

    @Test
    void getAllGames_shouldReturnEmptyPage_whenNoGamesExist() throws Exception {
        // Arrange
        Page<GameDTO> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 12), 0);
        when(gameService.getAll(any())).thenReturn(emptyPage);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$._embedded").doesNotExist());
    }

    @Test
    void getAllGames_shouldReturnFilteredResults_whenFiltersAreProvided() throws Exception {
        // Arrange
        GameDTO filteredGame = new GameDTO(
                2L,
                "Zelda Adventure 5",
                "A fantasy RPG",
                "zelda.png",
                Set.of(GenreType.RPG, GenreType.MMORPG),
                PublisherType.NINTENDO,
                Set.of(PlatformType.NINTENDO_SWITCH),
                LocalDateTime.now()
        );

        Page<GameDTO> filteredPage = new PageImpl<>(List.of(filteredGame), PageRequest.of(0, 12), 1);
        when(gameService.getAll(any())).thenReturn(filteredPage);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/games")
                        .param("title", "Zelda")
                        .param("genres", "RPG,MMORPG")
                        .param("publisher", "NINTENDO")
                        .param("platforms", "NINTENDO_SWITCH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.gameDTOList[0].title").value("Zelda Adventure 5"))
                .andExpect(jsonPath("$._embedded.gameDTOList[0].publisher").value("NINTENDO"))
                .andExpect(jsonPath("$._embedded.gameDTOList[0].platforms[0]").value("NINTENDO_SWITCH"));
    }

    @Test
    void getAllGames_shouldReturnBadRequest_whenPageOutOfRange() throws Exception {
        // Arrange
        when(gameService.getAll(any())).thenThrow(new Exceptions.PageOutOfRangeException("Page out of range"));

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/games").param("page", "999"))
                .andExpect(status().isBadRequest());
    }

}
