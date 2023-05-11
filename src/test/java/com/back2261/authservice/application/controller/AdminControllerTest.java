package com.back2261.authservice.application.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.back2261.authservice.domain.service.DefaultAdminService;
import com.back2261.authservice.interfaces.dto.GamerDto;
import com.back2261.authservice.interfaces.dto.GamerResponseBody;
import com.back2261.authservice.interfaces.dto.MessageDto;
import com.back2261.authservice.interfaces.dto.MessageResponseBody;
import com.back2261.authservice.interfaces.request.GameRequest;
import com.back2261.authservice.interfaces.request.KeywordRequest;
import com.back2261.authservice.interfaces.response.GamerResponse;
import com.back2261.authservice.interfaces.response.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.GameBuddyDevs.backendlibrary.base.BaseBody;
import io.github.GameBuddyDevs.backendlibrary.base.Status;
import io.github.GameBuddyDevs.backendlibrary.enums.TransactionCode;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageBody;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import io.github.GameBuddyDevs.backendlibrary.service.JwtService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(
        value = AdminController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DefaultAdminService defaultAdminService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() {
        token = "test";
    }

    @Test
    void testGetBlockedUsers_whenValidTokenProvided_shouldReturnBlockedUsers() throws Exception {
        GamerResponse gamerResponse = new GamerResponse();
        GamerResponseBody body = new GamerResponseBody();
        List<GamerDto> blockedUsers = new ArrayList<>();
        GamerDto gamerDto = new GamerDto();
        gamerDto.setUserId("test");
        gamerDto.setUsername("test");
        gamerDto.setEmail("test");
        gamerDto.setAvatar("test");
        gamerDto.setCreatedDate(new Date());
        blockedUsers.add(gamerDto);
        body.setBlockedUsers(blockedUsers);
        gamerResponse.setBody(new BaseBody<>(body));
        gamerResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultAdminService.getBlockedUsers(Mockito.anyString())).thenReturn(gamerResponse);

        var request = MockMvcRequestBuilders.get("/admin/get/blocked/users")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        GamerResponse actualResponse = objectMapper.readValue(responseJson, GamerResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(
                gamerResponse.getBody().getData().getBlockedUsers().size(),
                actualResponse.getBody().getData().getBlockedUsers().size());
    }

    @Test
    void testGetReportedMessages_whenValidTokenProvided_shouldReturnReportedMessages() throws Exception {
        MessageResponse messageResponse = new MessageResponse();
        MessageResponseBody body = new MessageResponseBody();
        List<MessageDto> reportedMessages = new ArrayList<>();
        MessageDto messageDto = new MessageDto();
        messageDto.setMessage("test");
        messageDto.setMessageDate(new Date());
        messageDto.setId(UUID.randomUUID().toString());
        messageDto.setSenderId(UUID.randomUUID().toString());
        reportedMessages.add(messageDto);
        body.setReportedMessages(reportedMessages);
        messageResponse.setBody(new BaseBody<>(body));
        messageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultAdminService.getReportedMessages(Mockito.anyString()))
                .thenReturn(messageResponse);

        var request = MockMvcRequestBuilders.get("/admin/get/reported/messages")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        MessageResponse actualResponse = objectMapper.readValue(responseJson, MessageResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(
                messageResponse.getBody().getData().getReportedMessages().size(),
                actualResponse.getBody().getData().getReportedMessages().size());
    }

    @Test
    void testBlockUser_whenValidUserIdAndTokenProvided_shouldReturnSuccessMessage() throws Exception {
        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody defaultMessageBody = new DefaultMessageBody("test");
        defaultMessageResponse.setBody(new BaseBody<>(defaultMessageBody));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultAdminService.banUser(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/admin/ban/user/test")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testUnblockUser_whenValidUserIdAndTokenProvided_shouldReturnSuccessMessage() throws Exception {
        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody defaultMessageBody = new DefaultMessageBody("test");
        defaultMessageResponse.setBody(new BaseBody<>(defaultMessageBody));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultAdminService.unbanUser(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/admin/unban/user/test")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testAddGame_whenValidTokenAndGameInfoProvided_shouldReturnSuccessMessage() throws Exception {
        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody defaultMessageBody = new DefaultMessageBody("test");
        defaultMessageResponse.setBody(new BaseBody<>(defaultMessageBody));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        GameRequest gameRequest = new GameRequest();
        gameRequest.setGameDescription("test");
        gameRequest.setGameName("test");
        gameRequest.setGameIcon("test");
        gameRequest.setRating(5.3f);
        gameRequest.setCategory("test");

        Mockito.when(defaultAdminService.addGame(Mockito.any(), Mockito.any(GameRequest.class)))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/admin/add/game")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(gameRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testAddKeyword_whenValidTokenAndKeywordProvided_shouldReturnSuccessMessage() throws Exception {
        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody defaultMessageBody = new DefaultMessageBody("test");
        defaultMessageResponse.setBody(new BaseBody<>(defaultMessageBody));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        KeywordRequest keywordRequest = new KeywordRequest();
        keywordRequest.setKeyword("test");
        keywordRequest.setDescription("test");

        Mockito.when(defaultAdminService.addKeyword(Mockito.any(), Mockito.any(KeywordRequest.class)))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/admin/add/keyword")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(keywordRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testDeleteReportedMessage_whenValidTokenAndMessageIdProvided_shouldReturnSuccessMessage() throws Exception {
        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody defaultMessageBody = new DefaultMessageBody("test");
        defaultMessageResponse.setBody(new BaseBody<>(defaultMessageBody));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultAdminService.deleteReportedMessage(Mockito.any(), Mockito.anyString()))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.delete("/admin/delete/reported/message/test")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }
}
