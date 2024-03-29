package com.back2261.authservice.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import com.back2261.authservice.infrastructure.entity.*;
import com.back2261.authservice.infrastructure.repository.*;
import com.back2261.authservice.interfaces.request.*;
import com.back2261.authservice.interfaces.response.*;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    private AvatarsRepository avatarsRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private UpdateDataFeignService updateDataFeignService;

    private String token;

    @BeforeEach
    void setUp() {
        token = "test";
    }

    @Test
    void testLogin_whenUserNotFound_ReturnError103() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("test");
        loginRequest.setUsernameOrEmail("test");

        Mockito.when(gamerRepository.findByGamerUsername(anyString())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(loginRequest));
        assertEquals(103, exception.getTransactionCode().getId());
    }

    @Test
    void testLogin_whenPasswordIsWrong_ReturnError108() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("test");
        loginRequest.setUsernameOrEmail("test@test.com");
        Gamer gamer = getGamer();

        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(gamer));
        Mockito.doThrow(new BadCredentialsException("test"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(loginRequest));
        assertEquals(108, exception.getTransactionCode().getId());
    }

    @Test
    void testLogin_whenUserBlocked_ReturnError113() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("test");
        loginRequest.setUsernameOrEmail("test");
        Gamer gamer = getGamer();
        gamer.setIsBlocked(true);

        Mockito.when(gamerRepository.findByGamerUsername(anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(gamer, null, new ArrayList<>()));

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(loginRequest));
        assertEquals(113, exception.getTransactionCode().getId());
    }

    @Test
    void testLogin_whenUserNotVerified_ReturnError106() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("test");
        loginRequest.setUsernameOrEmail("test");
        Gamer gamer = getGamer();
        gamer.setIsVerified(false);

        Mockito.when(gamerRepository.findByGamerUsername(anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(gamer, null, new ArrayList<>()));

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(loginRequest));
        assertEquals(106, exception.getTransactionCode().getId());
    }

    @Test
    void testLogin_whenUserNotCompleted_ReturnError109() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("test");
        loginRequest.setUsernameOrEmail("test");
        Gamer gamer = getGamer();
        gamer.setIsRegistered(false);

        Mockito.when(gamerRepository.findByGamerUsername(anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(gamer, null, new ArrayList<>()));

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(loginRequest));
        assertEquals(109, exception.getTransactionCode().getId());
    }

    @Test
    void testLogin_whenLoginWithUsername_ReturnSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("test");
        loginRequest.setUsernameOrEmail("test");
        Gamer gamer = getGamer();
        List<Session> sessions = new ArrayList<>();

        Mockito.when(gamerRepository.findByGamerUsername(anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(gamer, null, new ArrayList<>()));
        Mockito.when(jwtService.generateToken(any(Gamer.class))).thenReturn("test");
        Mockito.when(jwtService.extractExpiration(anyString())).thenReturn(new Date());
        Mockito.when(sessionRepository.findAllByEmail(anyString())).thenReturn(sessions);
        Mockito.doNothing().when(sessionRepository).deleteAll(sessions);

        LoginResponse result = authService.login(loginRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testLogin_whenLoginWithEmail_ReturnSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("test");
        loginRequest.setUsernameOrEmail("test@test.com");
        Gamer gamer = getGamer();
        List<Session> sessions = new ArrayList<>();

        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(gamer, null, new ArrayList<>()));
        Mockito.when(jwtService.generateToken(any(Gamer.class))).thenReturn("test");
        Mockito.when(jwtService.extractExpiration(anyString())).thenReturn(new Date());
        Mockito.when(sessionRepository.findAllByEmail(anyString())).thenReturn(sessions);
        Mockito.doNothing().when(sessionRepository).deleteAll(sessions);

        LoginResponse result = authService.login(loginRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testRegister_whenEmailAlreadyExist_ReturnError101() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@test.com");
        registerRequest.setPassword("test");

        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(getGamer()));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.register(registerRequest));
        assertEquals(101, exception.getTransactionCode().getId());
    }

    @Test
    void testRegister_whenEmailSendFails_ReturnError102() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@test.com");
        registerRequest.setPassword("test");

        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("test");
        Mockito.doThrow(new RuntimeException("test")).when(emailSender).send(any(SimpleMailMessage.class));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.register(registerRequest));
        assertEquals(102, exception.getTransactionCode().getId());
    }

    @Test
    void testRegister_whenValidRequestProvided_ReturnSuccess() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@test.com");
        registerRequest.setPassword("test");

        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("test");
        Mockito.doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        RegisterResponse result = authService.register(registerRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testVerifyCode_whenUserNotFound_ReturnError103() {
        VerifyRequest verifyRequest = new VerifyRequest();
        verifyRequest.setEmail("test@test.com");
        verifyRequest.setVerificationCode(123123);

        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.verifyCode(verifyRequest));
        assertEquals(103, exception.getTransactionCode().getId());
    }

    @Test
    void testVerifyCode_whenValidCodeNotFound_ReturnError105() {
        VerifyRequest verifyRequest = new VerifyRequest();
        verifyRequest.setEmail("test@test.com");
        verifyRequest.setVerificationCode(123123);

        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(getGamer()));
        Mockito.when(verificationCodeRepository.findByEmailAndCodeAndIsValidTrue(anyString(), anyInt()))
                .thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.verifyCode(verifyRequest));
        assertEquals(105, exception.getTransactionCode().getId());
    }

    @Test
    void testVerifyCode_whenValidRequestProvided_ReturnSuccess() {
        VerifyRequest verifyRequest = new VerifyRequest();
        verifyRequest.setEmail("test@test.com");
        verifyRequest.setVerificationCode(123123);
        List<Session> sessions = new ArrayList<>();

        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(getGamer()));
        Mockito.when(verificationCodeRepository.findByEmailAndCodeAndIsValidTrue(anyString(), anyInt()))
                .thenReturn(Optional.of(getVerificationCode()));
        Mockito.when(sessionRepository.findAllByEmail(anyString())).thenReturn(sessions);
        Mockito.doNothing().when(sessionRepository).deleteAll(sessions);
        Mockito.when(jwtService.generateToken(any(Gamer.class))).thenReturn("test");
        Mockito.when(jwtService.extractExpiration(anyString())).thenReturn(new Date());

        VerifyResponse result = authService.verifyCode(verifyRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testChangePwd_whenUserNotFound_ReturnError103() {
        ChangePwdRequest changePwdRequest = new ChangePwdRequest();
        changePwdRequest.setPassword("test");

        Mockito.when(jwtService.extractUsername(anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.changePwd(token, changePwdRequest));
        assertEquals(103, exception.getTransactionCode().getId());
    }

    @Test
    void testChangePwd_whenUserPasswordSame_ReturnError112() {
        ChangePwdRequest changePwdRequest = new ChangePwdRequest();
        changePwdRequest.setPassword("test");

        Mockito.when(jwtService.extractUsername(anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(getGamer()));
        Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.changePwd(token, changePwdRequest));
        assertEquals(112, exception.getTransactionCode().getId());
    }

    @Test
    void testChangePwd_whenValidRequestProvided_ReturnSuccess() {
        ChangePwdRequest changePwdRequest = new ChangePwdRequest();
        changePwdRequest.setPassword("test");

        Mockito.when(jwtService.extractUsername(anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(getGamer()));
        Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("test");

        DefaultMessageResponse result = authService.changePwd(token, changePwdRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testSetUsername_whenUserNotFound_ReturnError103() {
        UsernameRequest usernameRequest = new UsernameRequest();
        usernameRequest.setUsername("test");

        Mockito.when(jwtService.extractUsername(anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.setUsername(token, usernameRequest));
        assertEquals(103, exception.getTransactionCode().getId());
    }

    @Test
    void testSetUsername_whenUserNotVerified_ReturnError106() {
        UsernameRequest usernameRequest = new UsernameRequest();
        usernameRequest.setUsername("test");
        Gamer gamer = getGamer();
        gamer.setIsVerified(false);

        Mockito.when(jwtService.extractUsername(anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(gamer));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.setUsername(token, usernameRequest));
        assertEquals(106, exception.getTransactionCode().getId());
    }

    @Test
    void testSetUsername_whenUsernameAlreadyTakenByAnotherUser_ReturnError107() {
        UsernameRequest usernameRequest = new UsernameRequest();
        usernameRequest.setUsername("test");
        Gamer gamer = getGamer();
        Gamer gamer2 = getGamer();
        gamer2.setUserId("test2");

        Mockito.when(jwtService.extractUsername(anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findByGamerUsername(anyString())).thenReturn(Optional.of(gamer2));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.setUsername(token, usernameRequest));
        assertEquals(107, exception.getTransactionCode().getId());
    }

    @Test
    void testSetUsername_whenUserChangesCurrentUsername_ReturnSuccess() {
        UsernameRequest usernameRequest = new UsernameRequest();
        usernameRequest.setUsername("otherUsername");

        Mockito.when(jwtService.extractUsername(anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(getGamer()));
        Mockito.when(gamerRepository.findByGamerUsername(anyString())).thenReturn(Optional.empty());

        DefaultMessageResponse result = authService.setUsername(token, usernameRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testSetUsername_whenValidUsernameProvided_ReturnSuccess() {
        UsernameRequest usernameRequest = new UsernameRequest();
        usernameRequest.setUsername("test");

        Mockito.when(jwtService.extractUsername(anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(getGamer()));
        Mockito.when(gamerRepository.findByGamerUsername(anyString())).thenReturn(Optional.empty());

        DefaultMessageResponse result = authService.setUsername(token, usernameRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testDetails_whenUserNotFound_ReturnError103() {
        DetailsRequest detailsRequest = getDetailsRequest();
        Mockito.when(jwtService.extractUsername(anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.details(token, detailsRequest));
        assertEquals(103, exception.getTransactionCode().getId());
    }

    @Test
    void testDetails_whenUserNotVerified_ReturnError106() {
        DetailsRequest detailsRequest = getDetailsRequest();
        Gamer gamer = getGamer();
        gamer.setIsVerified(false);
        Mockito.when(jwtService.extractUsername(anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(gamer));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.details(token, detailsRequest));
        assertEquals(106, exception.getTransactionCode().getId());
    }

    @Test
    void testDetails_whenFeignServiceErrorOccur_ReturnError123() {
        DetailsRequest detailsRequest = getDetailsRequest();
        Gamer gamer = getGamer();

        Request request = Request.create(Request.HttpMethod.GET, "url", new HashMap<>(), null, new RequestTemplate());
        Mockito.when(jwtService.extractUsername(anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(keywordsRepository.findById(any(UUID.class))).thenReturn(Optional.of(getKeyword()));
        Mockito.when(gamesRepository.findById(anyString())).thenReturn(Optional.of(getGames()));
        Mockito.doThrow(new FeignException.BadRequest("test", request, null, null))
                .when(updateDataFeignService)
                .updateData();

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.details(token, detailsRequest));
        assertEquals(123, exception.getTransactionCode().getId());
    }

    @Test
    void testDetails_whenValidRequestProvided_ReturnSuccess() {
        DetailsRequest detailsRequest = getDetailsRequest();
        Gamer gamer = getGamer();
        FeignResponse feignResponse = new FeignResponse();
        feignResponse.setMessage("test");

        Mockito.when(jwtService.extractUsername(anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(keywordsRepository.findById(any(UUID.class))).thenReturn(Optional.of(getKeyword()));
        Mockito.when(gamesRepository.findById(anyString())).thenReturn(Optional.of(getGames()));
        Mockito.when(updateDataFeignService.updateData()).thenReturn(feignResponse);

        DefaultMessageResponse result = authService.details(token, detailsRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testValidateToken_whenTokenNotFound_ReturnCode111() {
        Mockito.when(sessionRepository.findById(anyString())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.validateToken(token));
        assertEquals(111, exception.getTransactionCode().getId());
    }

    @Test
    void testValidateToken_whenUserNotFound_ReturnError103() {
        Mockito.when(sessionRepository.findById(anyString())).thenReturn(Optional.of(getSession()));
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.validateToken(token));
        assertEquals(103, exception.getTransactionCode().getId());
    }

    @Test
    void testValidateToken_whenInValidTokenProvided_ReturnCode110() {
        Mockito.when(sessionRepository.findById(anyString())).thenReturn(Optional.of(getSession()));
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(getGamer()));
        Mockito.when(jwtService.validateToken(anyString(), any(UserDetails.class)))
                .thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.validateToken(token));
        assertEquals(110, exception.getTransactionCode().getId());
    }

    @Test
    void testValidateToken_whenValidTokenProvided_ReturnSuccess() {
        Mockito.when(sessionRepository.findById(anyString())).thenReturn(Optional.of(getSession()));
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(getGamer()));
        Mockito.when(jwtService.validateToken(anyString(), any(UserDetails.class)))
                .thenReturn(true);

        TokenResponse result = authService.validateToken(token);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testSendVerificationEmail_whenUserNotFound_ReturnError103() {
        SendCodeRequest sendCodeRequest = new SendCodeRequest();
        sendCodeRequest.setEmail("test@test.com");
        sendCodeRequest.setIsRegister(false);

        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.sendVerificationEmail(sendCodeRequest));
        assertEquals(103, exception.getTransactionCode().getId());
    }

    @Test
    void testSendVerificationEmail_whenErrorOccurWhileSendingMail_ReturnCode102() {
        SendCodeRequest sendCodeRequest = new SendCodeRequest();
        sendCodeRequest.setEmail("test@test.com");
        sendCodeRequest.setIsRegister(true);
        List<VerificationCode> verificationCodes = new ArrayList<>();
        verificationCodes.add(getVerificationCode());
        verificationCodes.add(getVerificationCode());

        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(getGamer()));
        Mockito.when(verificationCodeRepository.findAllByEmail(anyString())).thenReturn(verificationCodes);
        Mockito.doThrow(new RuntimeException("test")).when(emailSender).send(any(SimpleMailMessage.class));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.sendVerificationEmail(sendCodeRequest));
        assertEquals(102, exception.getTransactionCode().getId());
    }

    @Test
    void testSendVerificationEmail_whenSendForRegister_ReturnSuccess() {
        SendCodeRequest sendCodeRequest = new SendCodeRequest();
        sendCodeRequest.setEmail("test@test.com");
        sendCodeRequest.setIsRegister(true);
        List<VerificationCode> verificationCodes = new ArrayList<>();
        verificationCodes.add(getVerificationCode());
        verificationCodes.add(getVerificationCode());

        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(getGamer()));
        Mockito.when(verificationCodeRepository.findAllByEmail(anyString())).thenReturn(verificationCodes);
        Mockito.doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        DefaultMessageResponse result = authService.sendVerificationEmail(sendCodeRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testSendVerificationEmail_whenSendForForgotPwd_ReturnSuccess() {
        SendCodeRequest sendCodeRequest = new SendCodeRequest();
        sendCodeRequest.setEmail("test@test.com");
        sendCodeRequest.setIsRegister(false);
        List<VerificationCode> verificationCodes = new ArrayList<>();
        verificationCodes.add(getVerificationCode());
        verificationCodes.add(getVerificationCode());

        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(getGamer()));
        Mockito.when(verificationCodeRepository.findAllByEmail(anyString())).thenReturn(verificationCodes);
        Mockito.doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        DefaultMessageResponse result = authService.sendVerificationEmail(sendCodeRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testChangeAvatar_whenAvatarNotFound_ReturnErrorCode127() {
        ChangeAvatarRequest changeAvatarRequest = new ChangeAvatarRequest();
        changeAvatarRequest.setAvatarId(UUID.randomUUID().toString());

        Mockito.when(avatarsRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.changeAvatar(token, changeAvatarRequest));
        assertEquals(127, exception.getTransactionCode().getId());
    }

    @Test
    void testChangeAvatar_whenAvatarIsSpecialAndNotBought_ReturnErrorCode137() {
        Gamer gamer = getGamer();
        ChangeAvatarRequest changeAvatarRequest = new ChangeAvatarRequest();
        changeAvatarRequest.setAvatarId(UUID.randomUUID().toString());
        Avatars avatars = new Avatars();
        avatars.setIsSpecial(true);
        avatars.setImage("test");
        avatars.setId(UUID.randomUUID());
        avatars.setPrice(100);

        Mockito.when(avatarsRepository.findById(any(UUID.class))).thenReturn(Optional.of(avatars));
        Mockito.when(jwtService.extractUsername(anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(gamer));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> authService.changeAvatar(token, changeAvatarRequest));
        assertEquals(137, exception.getTransactionCode().getId());
    }

    @Test
    void testChangeAvatar_whenAvatarIsSpecialAndBought_ReturnSuccess() {
        Gamer gamer = getGamer();
        ChangeAvatarRequest changeAvatarRequest = new ChangeAvatarRequest();
        changeAvatarRequest.setAvatarId(UUID.randomUUID().toString());
        Avatars avatars = new Avatars();
        avatars.setIsSpecial(true);
        avatars.setImage("test");
        avatars.setId(UUID.randomUUID());
        avatars.setPrice(100);
        gamer.getBoughtAvatars().add(avatars);

        Mockito.when(avatarsRepository.findById(any(UUID.class))).thenReturn(Optional.of(avatars));
        Mockito.when(jwtService.extractUsername(anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(gamer));

        DefaultMessageResponse result = authService.changeAvatar(token, changeAvatarRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testChangeAvatar_whenFreeAvatarProvided_ReturnSuccess() {
        Gamer gamer = getGamer();
        ChangeAvatarRequest changeAvatarRequest = new ChangeAvatarRequest();
        changeAvatarRequest.setAvatarId(UUID.randomUUID().toString());
        Avatars avatars = new Avatars();
        avatars.setIsSpecial(false);
        avatars.setImage("test");
        avatars.setId(UUID.randomUUID());
        avatars.setPrice(100);

        Mockito.when(avatarsRepository.findById(any(UUID.class))).thenReturn(Optional.of(avatars));
        Mockito.when(jwtService.extractUsername(anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(gamer));

        DefaultMessageResponse result = authService.changeAvatar(token, changeAvatarRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testChangeAge_whenCalled_ReturnSuccess() {
        Gamer gamer = getGamer();
        ChangeAgeRequest changeAgeRequest = new ChangeAgeRequest();
        changeAgeRequest.setAge(35);

        Mockito.when(jwtService.extractUsername(anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(gamer));

        DefaultMessageResponse result = authService.changeAge(token, changeAgeRequest);
        assertEquals("100", result.getStatus().getCode());
        assertEquals(35, gamer.getAge());
    }

    @Test
    void testChangeGames_whenCalled_ReturnSuccess() {
        Gamer gamer = getGamer();
        Games games = getGames();
        List<String> gamesList = new ArrayList<>();
        gamesList.add("test");
        gamesList.add("test");
        gamesList.add("test");
        ChangeDetailRequest changeGamesRequest = new ChangeDetailRequest();
        changeGamesRequest.setGamesOrKeywordsList(gamesList);

        Mockito.when(jwtService.extractUsername(anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamesRepository.findById(anyString())).thenReturn(Optional.of(games));

        DefaultMessageResponse result = authService.changeGames(token, changeGamesRequest);
        assertEquals("100", result.getStatus().getCode());
        assertEquals(1, gamer.getLikedgames().size());
    }

    @Test
    void testChangeKeywords_whenCalled_ReturnSuccess() {
        Gamer gamer = getGamer();
        Keywords keyword = getKeyword();
        List<String> keywordsList = new ArrayList<>();
        keywordsList.add(UUID.randomUUID().toString());
        keywordsList.add(UUID.randomUUID().toString());
        keywordsList.add(UUID.randomUUID().toString());
        ChangeDetailRequest changeKeywordsRequest = new ChangeDetailRequest();
        changeKeywordsRequest.setGamesOrKeywordsList(keywordsList);

        Mockito.when(jwtService.extractUsername(anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(keywordsRepository.findById(any(UUID.class))).thenReturn(Optional.of(keyword));

        DefaultMessageResponse result = authService.changeKeywords(token, changeKeywordsRequest);
        assertEquals("100", result.getStatus().getCode());
        assertEquals(1, gamer.getKeywords().size());
    }

    private Games getGames() {
        Games games = new Games();
        games.setGameId("test");
        games.setGameName("test");
        games.setGameIcon("test");
        games.setCategory("test");
        games.setDescription("test");
        games.setAvgVote(1F);
        return games;
    }

    private Keywords getKeyword() {
        Keywords keyword = new Keywords();
        keyword.setId(UUID.randomUUID());
        keyword.setKeywordName("test");
        keyword.setCreatedDate(new Date());
        return keyword;
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

    private VerificationCode getVerificationCode() {
        VerificationCode code = new VerificationCode();
        code.setCode(123123);
        code.setEmail("test@test.com");
        code.setIsValid(true);
        code.setId(UUID.randomUUID());
        return code;
    }

    private DetailsRequest getDetailsRequest() {
        DetailsRequest detailsRequest = new DetailsRequest();
        detailsRequest.setAge(15);
        detailsRequest.setCountry("test");
        detailsRequest.setGender("E");
        detailsRequest.setAvatar(UUID.randomUUID().toString());
        List<String> keywords = new ArrayList<>();
        keywords.add(UUID.randomUUID().toString());
        keywords.add(UUID.randomUUID().toString());
        detailsRequest.setKeywords(keywords);
        List<String> favoriteGames = new ArrayList<>();
        favoriteGames.add("test");
        favoriteGames.add("test2");
        detailsRequest.setFavoriteGames(favoriteGames);
        return detailsRequest;
    }

    private Session getSession() {
        Session session = new Session();
        session.setTokenExpiredDate(new Date());
        session.setEmail("test@test.com");
        session.setAccessToken("test");
        session.setCreatedDate(new Date());
        return session;
    }
}
