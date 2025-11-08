package com.centennial.gamepickd.util;

import com.centennial.gamepickd.dtos.*;
import com.centennial.gamepickd.entities.*;
import com.centennial.gamepickd.util.enums.GenreType;
import com.centennial.gamepickd.util.enums.PlatformType;
import com.centennial.gamepickd.util.enums.PublisherType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public final Function<Game, GameDTO> gameToGameDTO = game ->  {

        Set<GenreType> genres = Optional.ofNullable(game.getGenres())
                .orElse(List.of())
                .stream()
                .map(Genre::getLabel)
                .collect(Collectors.toSet());

        Set<PlatformType> platforms = Optional.ofNullable(game.getPlatforms())
                .orElse(List.of())
                .stream()
                .map(Platform::getName)
                .collect(Collectors.toSet());

        PublisherType publisherName = Optional.ofNullable(game.getPublisher())
                .map(Publisher::getName)
                .orElse(null);

        return new GameDTO(
                game.getId(),
                game.getTitle(),
                game.getDescription(),
                game.getCoverImagePath(),
                genres,
                publisherName,
                platforms,
                game.getCreatedAt()
        );
    };

    public Review addReviewDtoToReview(AddReviewDTO addReviewDTO) {
        // Generate unique ID
        String reviewId = UUID.randomUUID().toString();

        // Generate ISO 8601 timestamp
        String timeStamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());

        // Map fields from DTO to entity
        return new Review(
                reviewId,
                timeStamp,
                addReviewDTO.comment(),
                addReviewDTO.rate(),
                addReviewDTO.username(),
                addReviewDTO.gameId()
        );
    }

    public ReviewDTO reviewToReviewDto(Review review) {
        return new ReviewDTO(
                review.getReviewId(),
                review.getTimeStamp(),
                review.getComment(),
                review.getRate(),
                review.getUsername(),
                review.getGameId()
        );
    }
}
