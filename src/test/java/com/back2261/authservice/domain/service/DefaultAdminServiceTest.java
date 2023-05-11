package com.back2261.authservice.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import com.back2261.authservice.infrastructure.entity.Gamer;
import com.back2261.authservice.infrastructure.entity.Message;
import com.back2261.authservice.infrastructure.repository.*;
import com.back2261.authservice.interfaces.request.GameRequest;
import com.back2261.authservice.interfaces.request.KeywordRequest;
import com.back2261.authservice.interfaces.response.GamerResponse;
import com.back2261.authservice.interfaces.response.MessageResponse;
import com.back2261.authservice.util.MessageStatus;
import io.github.GameBuddyDevs.backendlibrary.enums.Role;
import io.github.GameBuddyDevs.backendlibrary.exception.BusinessException;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import io.github.GameBuddyDevs.backendlibrary.service.JwtService;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultAdminServiceTest {

    @InjectMocks
    private DefaultAdminService defaultAdminService;

    @Mock
    private GamerRepository gamerRepository;

    @Mock
    private AvatarsRepository avatarsRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private GamesRepository gamesRepository;

    @Mock
    private KeywordsRepository keywordsRepository;

    @Mock
    private JwtService jwtService;

    private String token;

    @BeforeEach
    void setUp() {
        token = "test";
    }

    @Test
    void testGetBlockedUsers_whenUserNotFound_ReturnErrorCode103() {
        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        BusinessException businessException =
                assertThrows(BusinessException.class, () -> defaultAdminService.getBlockedUsers(token));
        assertEquals(103, businessException.getTransactionCode().getId());
    }

    @Test
    void testGetBlockedUsers_whenCalledByUser_ReturnErrorCode140() {
        Gamer gamer = getGamer();
        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));

        BusinessException businessException =
                assertThrows(BusinessException.class, () -> defaultAdminService.getBlockedUsers(token));
        assertEquals(140, businessException.getTransactionCode().getId());
    }

    @Test
    void testGetBlockedUsers_whenCalledByAdmin_ReturnBlockedUsers() {
        Gamer gamer = getGamer();
        gamer.setRole(Role.ADMIN);
        List<Gamer> blockedUsers = new ArrayList<>();
        blockedUsers.add(getGamer());
        blockedUsers.add(getGamer());

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findAllByIsBlockedTrue()).thenReturn(blockedUsers);
        Mockito.when(avatarsRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        GamerResponse result = defaultAdminService.getBlockedUsers(token);
        assertEquals("100", result.getStatus().getCode());
        assertEquals(2, result.getBody().getData().getBlockedUsers().size());
    }

    @Test
    void testGetReportedMessages_whenCalled_ReturnReportedMessages() {
        Gamer gamer = getGamer();
        gamer.setRole(Role.ADMIN);
        List<Message> reportedMessages = new ArrayList<>();
        reportedMessages.add(getMessage());
        reportedMessages.add(getMessage());

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(messageRepository.findAllByIsReportedTrue()).thenReturn(reportedMessages);

        MessageResponse result = defaultAdminService.getReportedMessages(token);
        assertEquals("100", result.getStatus().getCode());
        assertEquals(2, result.getBody().getData().getReportedMessages().size());
    }

    @Test
    void testBanUser_whenUserNotFound_ReturnErrorCode103() {
        Gamer admin = getGamer();
        admin.setRole(Role.ADMIN);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(admin));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        BusinessException businessException =
                assertThrows(BusinessException.class, () -> defaultAdminService.banUser(token, "test"));
        assertEquals(103, businessException.getTransactionCode().getId());
    }

    @Test
    void testBanUser_whenCalledValid_ReturnSuccessMessage() {
        Gamer admin = getGamer();
        admin.setRole(Role.ADMIN);
        Gamer user = getGamer();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(admin));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(sessionRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        DefaultMessageResponse result = defaultAdminService.banUser(token, "test");
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testUnbanUser_whenUserNotFound_ReturnErrorCode103() {
        Gamer admin = getGamer();
        admin.setRole(Role.ADMIN);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(admin));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        BusinessException businessException =
                assertThrows(BusinessException.class, () -> defaultAdminService.unbanUser(token, "test"));
        assertEquals(103, businessException.getTransactionCode().getId());
    }

    @Test
    void testUnbanUser_whenUserNotBanned_ReturnErrorCode119() {
        Gamer admin = getGamer();
        admin.setRole(Role.ADMIN);
        Gamer user = getGamer();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(admin));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));

        BusinessException businessException =
                assertThrows(BusinessException.class, () -> defaultAdminService.unbanUser(token, "test"));
        assertEquals(119, businessException.getTransactionCode().getId());
    }

    @Test
    void testUnbanUser_whenCalledValid_ReturnSuccessMessage() {
        Gamer admin = getGamer();
        admin.setRole(Role.ADMIN);
        Gamer user = getGamer();
        user.setIsBlocked(true);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(admin));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));

        DefaultMessageResponse result = defaultAdminService.unbanUser(token, "test");
        assertEquals("100", result.getStatus().getCode());
        assertFalse(user.getIsBlocked());
    }

    @Test
    void testAddGame_whenAdminAddsGame_ReturnSuccessMessage() {
        GameRequest gameRequest = new GameRequest();
        gameRequest.setGameIcon("test");
        gameRequest.setGameName("test");
        gameRequest.setGameDescription("test");
        gameRequest.setCategory("test");
        gameRequest.setRating(5f);
        Gamer admin = getGamer();
        admin.setRole(Role.ADMIN);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(admin));

        DefaultMessageResponse result = defaultAdminService.addGame(token, gameRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testAddKeyword_whenAdminAddsKeyword_ReturnSuccessMessage() {
        KeywordRequest keywordRequest = new KeywordRequest();
        keywordRequest.setKeyword("test");
        keywordRequest.setDescription("test");
        Gamer admin = getGamer();
        admin.setRole(Role.ADMIN);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(admin));

        DefaultMessageResponse result = defaultAdminService.addKeyword(token, keywordRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testDeleteReportedMessage_whenMessageNotFound_ReturnErrorCode141() {
        Gamer admin = getGamer();
        admin.setRole(Role.ADMIN);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(admin));
        Mockito.when(messageRepository.findByIdAndIsReportedTrue(Mockito.anyString()))
                .thenReturn(Optional.empty());

        BusinessException businessException =
                assertThrows(BusinessException.class, () -> defaultAdminService.deleteReportedMessage(token, "test"));
        assertEquals(141, businessException.getTransactionCode().getId());
    }

    @Test
    void testDeleteReportedMessage_whenCalledValid_ReturnSuccessMessage() {
        Gamer admin = getGamer();
        admin.setRole(Role.ADMIN);
        Message message = getMessage();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(admin));
        Mockito.when(messageRepository.findByIdAndIsReportedTrue(Mockito.anyString()))
                .thenReturn(Optional.of(message));

        DefaultMessageResponse result = defaultAdminService.deleteReportedMessage(token, "test");
        assertEquals("100", result.getStatus().getCode());
        assertFalse(message.getIsReported());
    }

    private Gamer getGamer() {
        Gamer gamer = new Gamer();
        gamer.setUserId("test");
        gamer.setGamerUsername("test");
        gamer.setEmail("test");
        gamer.setAge(15);
        gamer.setCountry("test");
        gamer.setAvatar(UUID.randomUUID());
        gamer.setCreatedDate(new Date());
        gamer.setLastModifiedDate(new Date());
        gamer.setPwd("test");
        gamer.setGender("E");
        gamer.setIsBlocked(false);
        gamer.setIsRegistered(true);
        gamer.setRole(Role.USER);
        gamer.setIsVerified(true);
        gamer.setLikedgames(new HashSet<>());
        gamer.setKeywords(new HashSet<>());
        gamer.setBoughtAvatars(new HashSet<>());
        return gamer;
    }

    private Message getMessage() {
        Message message = new Message();
        message.setIsReported(true);
        message.setId(UUID.randomUUID().toString());
        message.setMessageBody("test");
        message.setDate(new Date());
        message.setReceiver("test");
        message.setSender("test");
        message.setSenderName("test");
        message.setReceiverName("test");
        message.setChatId(UUID.randomUUID().toString());
        message.setStatus(MessageStatus.RECEIVED);
        return message;
    }
}
