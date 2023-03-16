package com.back2261.authservice.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import com.back2261.authservice.domain.jwt.JwtService;
import com.back2261.authservice.exception.BusinessException;
import com.back2261.authservice.infrastructure.entity.Gamer;
import com.back2261.authservice.infrastructure.entity.Games;
import com.back2261.authservice.infrastructure.entity.Keywords;
import com.back2261.authservice.infrastructure.repository.*;
import com.back2261.authservice.interfaces.enums.Role;
import com.back2261.authservice.interfaces.request.DetailsRequest;
import com.back2261.authservice.interfaces.request.UsernameRequest;
import com.back2261.authservice.interfaces.response.DefaultMessageResponse;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

@ExtendWith(MockitoExtension.class)
class DefaultAuthServiceTest {
    @InjectMocks
    private DefaultAuthService authService;

    @Mock
    private GamerRepository gamerRepository;

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @Mock
    private GamesRepository gamesRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private KeywordsRepository keywordsRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {}

    @Test
    void testSetUsername_whenValidRequestProvided_shouldReturnSuccess() {
        UsernameRequest request = new UsernameRequest();
        request.setUsername("test1");
        request.setUserId("test");
        Gamer gamer = getGamer();
        gamer.setIsVerified(true);
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findByGamerUsername(Mockito.anyString())).thenReturn(Optional.empty());
        DefaultMessageResponse result = authService.setUsername(request);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testSetUsername_whenUserNotFoundWithUserId_shouldThrow103() {
        UsernameRequest request = new UsernameRequest();
        request.setUsername("test1");
        request.setUserId("test");
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        BusinessException thrownError = assertThrows(BusinessException.class, () -> authService.setUsername(request));
        assertEquals(103, thrownError.getTransactionCode().getId());
    }

    @Test
    void testDetails_whenValidRequestProvided_shouldReturnSuccess() {
        DetailsRequest request = new DetailsRequest();
        request.setUserId("test1");
        request.setAge(20);
        request.setCountry("test1");
        request.setGender("M");

        byte[] avatar = new byte[] {4};
        request.setAvatar(avatar);

        List<String> keywords = new ArrayList<>();
        keywords.add("test");
        request.setKeywords(keywords);

        List<String> favGames = new ArrayList<>();
        favGames.add("test");
        request.setFavoriteGames(favGames);

        Gamer gamer = getGamer();
        gamer.setIsVerified(true);

        Keywords keyword = getKeywords();

        Games games = getGames();

        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(keywordsRepository.findByKeywordName(Mockito.anyString())).thenReturn(Optional.of(keyword));
        Mockito.when(gamesRepository.findByGameName(Mockito.anyString())).thenReturn(Optional.of(games));

        DefaultMessageResponse result = authService.details(request);

        assertEquals(20, gamer.getAge());
        assertEquals("test1", gamer.getCountry());
        assertEquals(avatar, gamer.getAvatar());
        assertEquals("M", gamer.getGender());
        assertTrue(gamer.getIsRegistered());
        assertEquals(
                favGames, gamer.getLikedgames().stream().map(Games::getGameName).collect(Collectors.toList()));
        assertEquals(
                keywords,
                gamer.getKeywords().stream().map(Keywords::getKeywordName).collect(Collectors.toList()));
        assertEquals("100", result.getStatus().getCode());
    }

    private Games getGames() {
        Games games = new Games();
        games.setGameId("test");
        games.setGameName("test");
        games.setGameIcon(new byte[0]);
        games.setCategory("test");
        games.setDescription("test");
        games.setAvgVote(1.00F);
        return games;
    }

    private Keywords getKeywords() {
        Keywords keywords = new Keywords();
        keywords.setId(new UUID(1, 0));
        keywords.setKeywordName("test");
        return keywords;
    }

    private Gamer getGamer() {
        Gamer gamer = new Gamer();
        gamer.setUserId("test");
        gamer.setGamerUsername("test");
        gamer.setEmail("test");
        gamer.setAge(15);
        gamer.setCountry("test");
        gamer.setAvatar(new byte[0]);
        gamer.setCreatedDate(new Date());
        gamer.setLastModifiedDate(new Date());
        gamer.setPwd("test");
        gamer.setGender("E");
        gamer.setIsBlocked(false);
        gamer.setIsRegistered(false);
        gamer.setRole(Role.USER);
        gamer.setIsVerified(false);
        gamer.setLikedgames(new HashSet<>());
        gamer.setKeywords(new HashSet<>());
        return gamer;
    }
}
