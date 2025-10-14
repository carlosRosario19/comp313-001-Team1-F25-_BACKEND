package com.centennial.gamepickd.services;

import com.centennial.gamepickd.dtos.AddGameDTO;
import com.centennial.gamepickd.entities.Contributor;
import com.centennial.gamepickd.entities.Game;
import com.centennial.gamepickd.entities.Genre;
import com.centennial.gamepickd.entities.User;
import com.centennial.gamepickd.repository.contracts.ContributorDAO;
import com.centennial.gamepickd.repository.contracts.GameDAO;
import com.centennial.gamepickd.repository.contracts.GenreDAO;
import com.centennial.gamepickd.services.contracts.StorageService;
import com.centennial.gamepickd.services.impl.GameServiceImpl;
import com.centennial.gamepickd.util.Exceptions;
import com.centennial.gamepickd.util.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GameServiceTest {
    @Mock
    private GameDAO gameDAO;

    @Mock
    private ContributorDAO contributorDAO;

    @Mock
    private GenreDAO genreDAO;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private GameServiceImpl gameService;

    private AddGameDTO validGameDTO;

    private MockMultipartFile coverImage;

    private Contributor mockContributor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mapper mapper = new Mapper();
        gameService = new GameServiceImpl(gameDAO, genreDAO, contributorDAO, storageService, mapper);

        coverImage = new MockMultipartFile(
                "coverImage",
                "cover.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy-image-data".getBytes()
        );
        // Create a sample AddBookDTO
        validGameDTO = new AddGameDTO(
                "test game title",
                "test game description",
                "RPG,MMORPG",
                "contributor",
                coverImage
        );
        mockContributor = new Contributor();
        mockContributor.setUser(new User());
        mockContributor.getUser().setUsername("contributor");
    }

    @Test
    void save_shouldThrowException_whenBookAlreadyExists() {
        // Arrange
        when(gameDAO.findByTitle(validGameDTO.title())).thenReturn(Optional.of(new Game()));

        // Act & Assert
        assertThrows(Exceptions.GameAlreadyExistsException.class, () -> gameService.add(validGameDTO));

        // Verify that no further interactions occur
        verify(gameDAO).findByTitle(validGameDTO.title());
        verifyNoMoreInteractions(gameDAO, contributorDAO, genreDAO);
    }

    // --- CASE 2: Contributor not found ---
    @Test
    void add_shouldThrowException_whenContributorNotFound() {
        when(gameDAO.findByTitle(validGameDTO.title())).thenReturn(Optional.empty());
        when(contributorDAO.findByUsername(validGameDTO.contributorUsername())).thenReturn(Optional.empty());

        assertThrows(Exceptions.ContributorNotFoundException.class,
                () -> gameService.add(validGameDTO));

        verify(contributorDAO).findByUsername(validGameDTO.contributorUsername());
        verifyNoMoreInteractions(genreDAO, storageService);
    }

    // --- CASE 3: Invalid genre ---
    @Test
    void add_shouldThrowException_whenInvalidGenreProvided() {
        AddGameDTO dto = new AddGameDTO(
                "new game",
                "desc",
                "INVALIDGENRE",
                "contributor",
                coverImage
        );

        when(gameDAO.findByTitle(dto.title())).thenReturn(Optional.empty());
        when(contributorDAO.findByUsername(dto.contributorUsername())).thenReturn(Optional.of(mockContributor));

        assertThrows(IllegalArgumentException.class, () -> gameService.add(dto));

        verify(contributorDAO).findByUsername(dto.contributorUsername());
        verifyNoInteractions(storageService);
    }

    // --- CASE 4: Storage exception during upload ---
    @Test
    void add_shouldThrowStorageException_whenUploadFails() throws Exception {
        when(gameDAO.findByTitle(validGameDTO.title())).thenReturn(Optional.empty());
        when(contributorDAO.findByUsername(validGameDTO.contributorUsername())).thenReturn(Optional.of(mockContributor));
        when(genreDAO.findByLabel(any())).thenReturn(Optional.of(new Genre()));

        doThrow(new RuntimeException("S3 down")).when(storageService).store(any());

        Exceptions.StorageException ex = assertThrows(
                Exceptions.StorageException.class,
                () -> gameService.add(validGameDTO)
        );

        assertTrue(ex.getMessage().contains("Failed to store cover image"));
        verify(storageService).store(any());
        verify(gameDAO, never()).create(any());
    }

    // --- CASE 5: Happy path ---
    @Test
    void add_shouldCreateGameSuccessfully_whenValidData() throws Exception {
        when(gameDAO.findByTitle(validGameDTO.title())).thenReturn(Optional.empty());
        when(contributorDAO.findByUsername(validGameDTO.contributorUsername())).thenReturn(Optional.of(mockContributor));
        when(genreDAO.findByLabel(any())).thenReturn(Optional.of(new Genre()));

        gameService.add(validGameDTO);

        verify(storageService).store(any());
        verify(gameDAO).create(any(Game.class));
    }

    // --- CASE 6: No cover image ---
    @Test
    void add_shouldWork_whenNoCoverImageProvided() throws Exception {
        AddGameDTO dtoWithoutImage = new AddGameDTO(
                "new title",
                "desc",
                "RPG",
                "contributor",
                null
        );

        when(gameDAO.findByTitle(dtoWithoutImage.title())).thenReturn(Optional.empty());
        when(contributorDAO.findByUsername(dtoWithoutImage.contributorUsername())).thenReturn(Optional.of(mockContributor));
        when(genreDAO.findByLabel(any())).thenReturn(Optional.of(new Genre()));

        gameService.add(dtoWithoutImage);

        verify(storageService, never()).store(any());
        verify(gameDAO).create(any(Game.class));
    }
}
