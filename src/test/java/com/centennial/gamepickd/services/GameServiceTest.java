package com.centennial.gamepickd.services;

import com.centennial.gamepickd.dtos.AddGameDTO;
import com.centennial.gamepickd.dtos.GameDTO;
import com.centennial.gamepickd.dtos.SearchGameDTO;
import com.centennial.gamepickd.entities.Contributor;
import com.centennial.gamepickd.entities.Game;
import com.centennial.gamepickd.entities.Genre;
import com.centennial.gamepickd.entities.User;
import com.centennial.gamepickd.repository.contracts.*;
import com.centennial.gamepickd.services.contracts.ReviewService;
import com.centennial.gamepickd.services.contracts.StorageService;
import com.centennial.gamepickd.services.impl.GameServiceImpl;
import com.centennial.gamepickd.util.Exceptions;
import com.centennial.gamepickd.util.Mapper;
import com.centennial.gamepickd.util.enums.GenreType;
import com.centennial.gamepickd.util.enums.PlatformType;
import com.centennial.gamepickd.util.enums.PublisherType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameServiceTest {
    @Mock
    private GameDAO gameDAO;

    @Mock
    private ContributorDAO contributorDAO;

    @Mock
    private GenreDAO genreDAO;

    @Mock
    private PublisherDAO publisherDAO;

    @Mock
    private PlatformDAO platformDAO;

    @Mock
    private ReviewService reviewService;

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
        gameService = new GameServiceImpl(gameDAO, genreDAO, contributorDAO, publisherDAO, platformDAO, storageService, reviewService, mapper);

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
                "UBISOFT",
                "PlayStation 5,Steam",
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
                "UBISOFT",
                "PlayStation 5",
                "contributor",
                coverImage
        );

        when(gameDAO.findByTitle(dto.title())).thenReturn(Optional.empty());
        when(contributorDAO.findByUsername(dto.contributorUsername())).thenReturn(Optional.of(mockContributor));
        when(publisherDAO.findByLabel(any())).thenReturn(Optional.of(mock(com.centennial.gamepickd.entities.Publisher.class)));
        when(platformDAO.findByLabels(any())).thenReturn(Set.of(mock(com.centennial.gamepickd.entities.Platform.class)));

        assertThrows(Exceptions.GenreNotFoundException.class, () -> gameService.add(dto));

        verify(contributorDAO).findByUsername(dto.contributorUsername());
        verifyNoInteractions(storageService);
    }

    // --- CASE 4: Storage exception during upload ---
    @Test
    void add_shouldThrowStorageException_whenUploadFails() throws Exception {
        when(gameDAO.findByTitle(validGameDTO.title())).thenReturn(Optional.empty());
        when(contributorDAO.findByUsername(validGameDTO.contributorUsername())).thenReturn(Optional.of(mockContributor));
        when(publisherDAO.findByLabel(any())).thenReturn(Optional.of(mock(com.centennial.gamepickd.entities.Publisher.class)));
        when(platformDAO.findByLabels(any())).thenReturn(Set.of(mock(com.centennial.gamepickd.entities.Platform.class)));
        when(genreDAO.findByLabels(any())).thenReturn(
                Set.of(new Genre())  // or multiple Genre mocks if needed
        );

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
        when(publisherDAO.findByLabel(any())).thenReturn(Optional.of(mock(com.centennial.gamepickd.entities.Publisher.class)));
        when(platformDAO.findByLabels(any())).thenReturn(Set.of(mock(com.centennial.gamepickd.entities.Platform.class)));
        when(genreDAO.findByLabels(any())).thenReturn(
                Set.of(new Genre())  // or multiple Genre mocks if needed
        );

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
                "UBISOFT",                       // Publisher
                "PlayStation 5",                 // Platform
                "contributor",
                null
        );

        when(gameDAO.findByTitle(dtoWithoutImage.title())).thenReturn(Optional.empty());
        when(contributorDAO.findByUsername(dtoWithoutImage.contributorUsername())).thenReturn(Optional.of(mockContributor));
        when(publisherDAO.findByLabel(any())).thenReturn(Optional.of(mock(com.centennial.gamepickd.entities.Publisher.class)));
        when(platformDAO.findByLabels(any())).thenReturn(Set.of(mock(com.centennial.gamepickd.entities.Platform.class)));
        when(genreDAO.findByLabels(any())).thenReturn(
                Set.of(new Genre())  // or multiple Genre mocks if needed
        );

        gameService.add(dtoWithoutImage);

        verify(storageService, never()).store(any());
        verify(gameDAO).create(any(Game.class));
    }

    @Test
    void add_shouldThrowPublisherNotFoundException_whenPublisherInvalid() {
        when(gameDAO.findByTitle(validGameDTO.title())).thenReturn(Optional.empty());
        when(contributorDAO.findByUsername(validGameDTO.contributorUsername())).thenReturn(Optional.of(mockContributor));
        when(publisherDAO.findByLabel(any())).thenReturn(Optional.empty());

        assertThrows(Exceptions.PublisherNotFoundException.class, () -> gameService.add(validGameDTO));
    }

    @Test
    void add_shouldThrowPlatformNotFoundException_whenPlatformInvalid() {
        when(gameDAO.findByTitle(validGameDTO.title())).thenReturn(Optional.empty());
        when(contributorDAO.findByUsername(validGameDTO.contributorUsername())).thenReturn(Optional.of(mockContributor));
        when(publisherDAO.findByLabel(any())).thenReturn(Optional.of(mock( com.centennial.gamepickd.entities.Publisher.class)));

        // Provide an invalid platform string
        AddGameDTO dtoInvalidPlatform = new AddGameDTO(
                validGameDTO.title(),
                validGameDTO.description(),
                validGameDTO.genres(),
                validGameDTO.publisher(),
                "INVALID_PLATFORM",
                validGameDTO.contributorUsername(),
                validGameDTO.coverImage()
        );

        assertThrows(Exceptions.PlatformNotFoundException.class, () -> gameService.add(dtoInvalidPlatform));
    }

    @Test
    void getAll_shouldReturnMappedPage_whenGamesExist() throws Exceptions.PageOutOfRangeException {
        SearchGameDTO dto = new SearchGameDTO(
                0, 10, null, null, null, null
        );

        Game game = new Game();
        game.setId(1L);
        game.setTitle("Test Game");
        game.setDescription("Desc");
        game.setCoverImagePath("cover.png");
        game.setCreatedAt(LocalDateTime.now());

        Page<Game> gamePage = new PageImpl<>(List.of(game));

        when(gameDAO.findAllOrderByPostedAtDesc(
                any(), any(), any(), any(), any(Pageable.class))
        ).thenReturn(gamePage);

        Page<GameDTO> result = gameService.getAll(dto);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        GameDTO dtoResult = result.getContent().get(0);
        assertEquals(game.getId(), dtoResult.id());
        assertEquals(game.getTitle(), dtoResult.title());
        assertEquals(game.getCoverImagePath(), dtoResult.coverImagePath());
        assertEquals(game.getCreatedAt(), dtoResult.postedAt());
    }

    // --- CASE 2: Invalid page number ---
    @Test
    void getAll_shouldThrow_whenPageNumberNegative() {
        SearchGameDTO dto = new SearchGameDTO(
                -1, 10, null, null, null, null
        );

        Exceptions.PageOutOfRangeException ex = assertThrows(
                Exceptions.PageOutOfRangeException.class,
                () -> gameService.getAll(dto)
        );
        assertTrue(ex.getMessage().contains("cannot be negative"));
    }

    // --- CASE 3: Invalid page size ---
    @Test
    void getAll_shouldThrow_whenPageSizeZero() {
        SearchGameDTO dto = new SearchGameDTO(
                0, 0, null, null, null, null
        );

        Exceptions.PageOutOfRangeException ex = assertThrows(
                Exceptions.PageOutOfRangeException.class,
                () -> gameService.getAll(dto)
        );
        assertTrue(ex.getMessage().contains("must be greater than 0"));
    }

    // --- CASE 4: Page size exceeds limit ---
    @Test
    void getAll_shouldThrow_whenPageSizeTooLarge() {
        SearchGameDTO dto = new SearchGameDTO(
                0, 101, null, null, null, null
        );

        Exceptions.PageOutOfRangeException ex = assertThrows(
                Exceptions.PageOutOfRangeException.class,
                () -> gameService.getAll(dto)
        );
        assertTrue(ex.getMessage().contains("cannot exceed"));
    }

    // --- CASE 5: Filters (title, genres, publisher, platforms) ---
    @Test
    void getAll_shouldPassAllFiltersToDAO() throws Exceptions.PageOutOfRangeException {
        SearchGameDTO dto = new SearchGameDTO(
                0,
                10,
                "Zelda",
                Set.of("RPG"),
                "NINTENDO",
                Set.of("NINTENDO_SWITCH")
        );

        Page<Game> emptyPage = new PageImpl<>(List.of());
        when(gameDAO.findAllOrderByPostedAtDesc(
                eq("Zelda"),
                eq(Set.of(GenreType.RPG)),
                eq(PublisherType.NINTENDO),
                eq(Set.of(PlatformType.NINTENDO_SWITCH)),
                any(Pageable.class)
        )).thenReturn(emptyPage);

        Page<GameDTO> result = gameService.getAll(dto);

        verify(gameDAO).findAllOrderByPostedAtDesc(
                eq("Zelda"),
                eq(Set.of(GenreType.RPG)),
                eq(PublisherType.NINTENDO),
                eq(Set.of(PlatformType.NINTENDO_SWITCH)),
                any(Pageable.class)
        );

        assertEquals(0, result.getTotalElements());
    }
}
