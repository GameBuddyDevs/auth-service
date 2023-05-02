package com.back2261.authservice.application.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.back2261.authservice.domain.service.DefaultAuthService;
import com.back2261.authservice.interfaces.dto.LoginResponseBody;
import com.back2261.authservice.interfaces.dto.RegisterResponseBody;
import com.back2261.authservice.interfaces.dto.TokenResponseBody;
import com.back2261.authservice.interfaces.dto.VerifyResponseBody;
import com.back2261.authservice.interfaces.request.*;
import com.back2261.authservice.interfaces.response.LoginResponse;
import com.back2261.authservice.interfaces.response.RegisterResponse;
import com.back2261.authservice.interfaces.response.TokenResponse;
import com.back2261.authservice.interfaces.response.VerifyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.GameBuddyDevs.backendlibrary.base.BaseBody;
import io.github.GameBuddyDevs.backendlibrary.base.Status;
import io.github.GameBuddyDevs.backendlibrary.enums.TransactionCode;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageBody;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import io.github.GameBuddyDevs.backendlibrary.service.JwtService;
import java.util.ArrayList;
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
        value = AuthController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DefaultAuthService defaultAuthService;

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
    void testLogin_whenValidLoginRequestProvided_shouldReturnTokenAndUserId() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("test");
        loginRequest.setUsernameOrEmail("test");

        LoginResponse loginResponse = new LoginResponse();
        LoginResponseBody loginResponseBody = new LoginResponseBody();
        loginResponseBody.setAccessToken(token);
        loginResponseBody.setUserId("test");
        loginResponse.setBody(new BaseBody<>(loginResponseBody));
        loginResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultAuthService.login(Mockito.any(LoginRequest.class))).thenReturn(loginResponse);

        var request = MockMvcRequestBuilders.post("/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        LoginResponse responseObj = objectMapper.readValue(responseJson, LoginResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(
                loginResponseBody.getAccessToken(),
                responseObj.getBody().getData().getAccessToken());
        assertEquals(
                loginResponseBody.getUserId(), responseObj.getBody().getData().getUserId());
    }

    @Test
    void testRegister_whenValidRegisterRequestProvided_shouldReturnUserId() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPassword("test");
        registerRequest.setEmail("test@test.com");
        registerRequest.setFcmToken("test");

        RegisterResponse registerResponse = new RegisterResponse();
        RegisterResponseBody registerResponseBody = new RegisterResponseBody();
        registerResponseBody.setUserId("test");
        registerResponse.setBody(new BaseBody<>(registerResponseBody));
        registerResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultAuthService.register(Mockito.any(RegisterRequest.class)))
                .thenReturn(registerResponse);

        var request = MockMvcRequestBuilders.post("/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(registerRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        RegisterResponse responseObj = objectMapper.readValue(responseJson, RegisterResponse.class);
        assertEquals(201, response.getResponse().getStatus());
        assertEquals(
                registerResponseBody.getUserId(),
                responseObj.getBody().getData().getUserId());
    }

    @Test
    void testVerifyCode_whenValidVerifyRequestProvided_shouldReturnTokenAndUserId() throws Exception {
        VerifyRequest verifyRequest = new VerifyRequest();
        verifyRequest.setEmail("test@test.com");
        verifyRequest.setVerificationCode(123123);

        VerifyResponse verifyResponse = new VerifyResponse();
        VerifyResponseBody verifyResponseBody = new VerifyResponseBody();
        verifyResponseBody.setAccessToken(token);
        verifyResponseBody.setUserId("test");
        verifyResponse.setBody(new BaseBody<>(verifyResponseBody));
        verifyResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultAuthService.verifyCode(Mockito.any(VerifyRequest.class)))
                .thenReturn(verifyResponse);

        var request = MockMvcRequestBuilders.post("/auth/verify")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(verifyRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        VerifyResponse responseObj = objectMapper.readValue(responseJson, VerifyResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(
                verifyResponseBody.getAccessToken(),
                responseObj.getBody().getData().getAccessToken());
    }

    @Test
    void testSetUsername_whenValidTokenAndUsernameProvided_shouldReturnSuccessMessage() throws Exception {
        UsernameRequest usernameRequest = new UsernameRequest();
        usernameRequest.setUsername("test");

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody defaultMessageBody = new DefaultMessageBody("test");
        defaultMessageResponse.setBody(new BaseBody<>(defaultMessageBody));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultAuthService.setUsername(Mockito.anyString(), Mockito.any(UsernameRequest.class)))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/auth/username")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(usernameRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testDetails_whenValidTokenAndDetailsProvided_shouldReturnSuccessMessage() throws Exception {
        DetailsRequest detailsRequest = new DetailsRequest();
        detailsRequest.setAge(20);
        detailsRequest.setGender("E");
        detailsRequest.setAvatar("test");
        detailsRequest.setCountry("test");
        List<String> dummyList = new ArrayList<>();
        dummyList.add("test");
        dummyList.add("test2");
        dummyList.add("test3");
        detailsRequest.setFavoriteGames(dummyList); // min 1
        detailsRequest.setKeywords(dummyList); // min 3

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody defaultMessageBody = new DefaultMessageBody("test");
        defaultMessageResponse.setBody(new BaseBody<>(defaultMessageBody));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultAuthService.details(Mockito.anyString(), Mockito.any(DetailsRequest.class)))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/auth/details")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(detailsRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testValidateToken_whenValidTokenProvided_shouldReturnUsernameAndTrue() throws Exception {
        TokenResponse tokenResponse = new TokenResponse();
        TokenResponseBody tokenResponseBody = new TokenResponseBody();
        tokenResponseBody.setIsValid(true);
        tokenResponseBody.setUsername("test");
        tokenResponse.setBody(new BaseBody<>(tokenResponseBody));

        Mockito.when(defaultAuthService.validateToken(Mockito.anyString())).thenReturn(tokenResponse);

        var request = MockMvcRequestBuilders.post("/auth/validateToken")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        TokenResponse responseObj = objectMapper.readValue(responseJson, TokenResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(
                tokenResponseBody.getUsername(), responseObj.getBody().getData().getUsername());
    }

    @Test
    void testSendCode_whenValidEmailProvided_shouldReturnSuccessMessage() throws Exception {
        SendCodeRequest sendCodeRequest = new SendCodeRequest();
        sendCodeRequest.setEmail("test@test.com");
        sendCodeRequest.setIsRegister(true);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody defaultMessageBody = new DefaultMessageBody("test");
        defaultMessageResponse.setBody(new BaseBody<>(defaultMessageBody));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultAuthService.sendVerificationEmail(Mockito.any(SendCodeRequest.class)))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/auth/sendCode")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(sendCodeRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testChangePwd_whenValidPasswordProvided_shouldReturnSuccessMessage() throws Exception {
        ChangePwdRequest changePwdRequest = new ChangePwdRequest();
        changePwdRequest.setPassword("test");

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody defaultMessageBody = new DefaultMessageBody("test");
        defaultMessageResponse.setBody(new BaseBody<>(defaultMessageBody));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultAuthService.changePwd(Mockito.anyString(), Mockito.any(ChangePwdRequest.class)))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.put("/auth/change/pwd")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(changePwdRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testChangeAvatar_whenValidAvatarIdProvided_shouldReturnSuccessMessage() throws Exception {
        ChangeAvatarRequest changeAvatarRequest = new ChangeAvatarRequest();
        changeAvatarRequest.setAvatarId(UUID.randomUUID().toString());

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody defaultMessageBody = new DefaultMessageBody("test");
        defaultMessageResponse.setBody(new BaseBody<>(defaultMessageBody));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultAuthService.changeAvatar(Mockito.anyString(), Mockito.any(ChangeAvatarRequest.class)))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.put("/auth/change/avatar")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(changeAvatarRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }
}
