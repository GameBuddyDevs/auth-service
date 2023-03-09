package com.back2261.authservice.domain.service;

import com.back2261.authservice.base.BaseBody;
import com.back2261.authservice.base.Status;
import com.back2261.authservice.domain.jwt.JwtService;
import com.back2261.authservice.exception.BusinessException;
import com.back2261.authservice.infrastructure.entity.*;
import com.back2261.authservice.infrastructure.repository.*;
import com.back2261.authservice.interfaces.dto.DefaultMessageBody;
import com.back2261.authservice.interfaces.dto.LoginResponseBody;
import com.back2261.authservice.interfaces.dto.RegisterResponseBody;
import com.back2261.authservice.interfaces.dto.TokenResponseBody;
import com.back2261.authservice.interfaces.enums.Role;
import com.back2261.authservice.interfaces.enums.TransactionCode;
import com.back2261.authservice.interfaces.request.*;
import com.back2261.authservice.interfaces.response.DefaultMessageResponse;
import com.back2261.authservice.interfaces.response.LoginResponse;
import com.back2261.authservice.interfaces.response.RegisterResponse;
import com.back2261.authservice.interfaces.response.TokenResponse;
import com.back2261.authservice.util.Constants;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {
    private final GamerRepository gamerRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final GamesRepository gamesRepository;
    private final SessionRepository sessionRepository;
    private final KeywordsRepository keywordsRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String usernameOrEmail = loginRequest.getUsernameOrEmail();
        String password = loginRequest.getPassword();
        Optional<Gamer> gamerOptional;
        if (usernameOrEmail.contains("@")) {
            gamerOptional = gamerRepository.findByEmail(usernameOrEmail);
        } else {
            gamerOptional = gamerRepository.findByGamerUsername(usernameOrEmail);
        }
        if (gamerOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.USER_NOT_FOUND);
        }
        Gamer gamer = gamerOptional.get();
        String email = gamer.getEmail();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException e) {
            throw new BusinessException(TransactionCode.WRONG_PASSWORD);
        }

        if (!gamer.getIsVerified()) {
            throw new BusinessException(TransactionCode.USER_NOT_VERIFIED);
        }
        if (!gamer.getIsRegistered()) {
            throw new BusinessException(TransactionCode.USER_NOT_COMPLETED);
        }

        LoginResponse loginResponse = new LoginResponse();
        LoginResponseBody body = new LoginResponseBody();

        String token = jwtService.generateToken(gamer);
        Date expirationDate = jwtService.extractExpiration(token);

        List<Session> foundSessions = sessionRepository.findByEmailAndIsActiveTrue(email);
        foundSessions.forEach(session -> {
            session.setIsActive(false);
            sessionRepository.save(session);
        });

        Session session = new Session();
        session.setAccessToken(token);
        session.setAccessExpiredDate(expirationDate);
        session.setIsActive(true);
        session.setEmail(email);
        sessionRepository.save(session);

        body.setAccessToken(token);
        body.setAccessTokenExpirationDate(expirationDate);
        loginResponse.setBody(new BaseBody<>(body));
        loginResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return loginResponse;
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        Optional<Gamer> gamerOptional = gamerRepository.findByEmail(email);
        if (gamerOptional.isPresent()) {
            throw new BusinessException(TransactionCode.EMAIL_EXISTS);
        }
        Gamer newGamer = new Gamer();
        newGamer.setUserId(UUID.randomUUID().toString());
        newGamer.setEmail(email);
        String encodedPassword = passwordEncoder.encode(password);
        newGamer.setPwd(encodedPassword);
        newGamer.setRole(Role.USER);
        gamerRepository.save(newGamer);
        String token = jwtService.generateToken(newGamer);
        Date expirationDate = jwtService.extractExpiration(token);
        Session session = new Session();
        session.setAccessToken(token);
        session.setAccessExpiredDate(expirationDate);
        session.setIsActive(true);
        session.setEmail(email);
        sessionRepository.save(session);

        sendVerificationEmail(email);
        RegisterResponse registerResponse = new RegisterResponse();
        RegisterResponseBody registerResponseBody = new RegisterResponseBody();
        registerResponseBody.setUserId(newGamer.getUserId());
        registerResponseBody.setToken(token);
        registerResponse.setBody(new BaseBody<>(registerResponseBody));
        registerResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return registerResponse;
    }

    @Override
    public DefaultMessageResponse verifyCode(VerifyRequest verifyRequest) {
        String userId = verifyRequest.getUserId();
        Integer code = verifyRequest.getVerificationCode();
        Optional<Gamer> gamerOptional = gamerRepository.findById(userId);
        if (gamerOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.USER_NOT_FOUND);
        }
        Gamer gamer = gamerOptional.get();
        if (gamer.getIsVerified()) {
            throw new BusinessException(TransactionCode.USER_ALREADY_VERIFIED);
        }
        String email = gamer.getEmail();
        Optional<VerificationCode> verificationCodeOptional =
                verificationCodeRepository.findByEmailAndCodeAndIsValidTrue(email, code);
        if (verificationCodeOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.VERIFICATION_CODE_NOT_FOUND);
        }
        VerificationCode verificationCode = verificationCodeOptional.get();
        verificationCode.setIsValid(false);
        verificationCodeRepository.save(verificationCode);

        gamer.setIsVerified(true);
        gamerRepository.save(gamer);
        DefaultMessageResponse verifyResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("User verified successfully");
        verifyResponse.setBody(new BaseBody<>(body));
        verifyResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return verifyResponse;
    }

    @Override
    public DefaultMessageResponse setUsername(UsernameRequest usernameRequest) {
        String username = usernameRequest.getUsername();
        String userId = usernameRequest.getUserId();
        Gamer gamer = checkGamer(userId);
        Optional<Gamer> usernameCheckOptional = gamerRepository.findByGamerUsername(username);

        if (usernameCheckOptional.isPresent()) {
            Gamer usernameCheck = usernameCheckOptional.get();
            if (!Objects.equals(usernameCheck.getUserId(), gamer.getUserId())) {
                throw new BusinessException(TransactionCode.USERNAME_EXISTS);
            }
        }

        if (!Objects.equals(gamer.getUsername(), username)) {
            gamer.setGamerUsername(username);
            gamerRepository.save(gamer);
        }

        DefaultMessageResponse verifyResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Username set successfully");
        verifyResponse.setBody(new BaseBody<>(body));
        verifyResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return verifyResponse;
    }

    @Override
    public DefaultMessageResponse details(DetailsRequest detailsRequest) {
        String userId = detailsRequest.getUserId();
        Integer age = detailsRequest.getAge();
        String country = detailsRequest.getCountry();
        byte[] avatar = detailsRequest.getAvatar();
        List<String> keyWords = detailsRequest.getKeywords();
        List<String> favGames = detailsRequest.getFavoriteGames();

        Gamer gamer = checkGamer(userId);
        gamer.setAge(age);
        gamer.setCountry(country);
        gamer.setAvatar(avatar);
        mapAndSetKeywords(gamer, keyWords);
        mapAndSetUserGames(gamer, favGames);
        gamer.setIsRegistered(true);
        gamerRepository.save(gamer);

        DefaultMessageResponse verifyResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("User details fetched successfully");
        verifyResponse.setBody(new BaseBody<>(body));
        verifyResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return verifyResponse;
    }

    @Override
    public TokenResponse validateToken(String token) {
        Optional<Session> sessionOptional = sessionRepository.findByAccessTokenAndIsActiveTrue(token);
        if (sessionOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.TOKEN_NOT_FOUND);
        }
        Session session = sessionOptional.get();
        String email = session.getEmail();
        Optional<Gamer> gamerOptional = gamerRepository.findByEmail(email);
        if (gamerOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.USER_NOT_FOUND);
        }
        Gamer gamer = gamerOptional.get();
        Boolean isValid = jwtService.validateToken(token, gamer);
        if (Boolean.FALSE.equals(isValid)) {
            throw new BusinessException(TransactionCode.TOKEN_INVALID);
        }

        TokenResponse tokenResponse = new TokenResponse();
        TokenResponseBody body = new TokenResponseBody();
        body.setUsername(gamer.getGamerUsername());
        body.setIsValid(true);
        tokenResponse.setBody(new BaseBody<>(body));
        tokenResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return tokenResponse;
    }

    private void sendVerificationEmail(String email) {
        List<VerificationCode> verificationCodes = verificationCodeRepository.findAllByEmail(email);
        if (verificationCodes.size() > 0) {
            verificationCodes.forEach(verificationCode -> verificationCode.setIsValid(false));
        }
        Integer code = new Random().nextInt(900000) + 100000;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(email);
            message.setSubject(String.format(Constants.EMAIL_SUBJECT, code));
            message.setText(String.format(Constants.EMAIL_TEXT, code));
            emailSender.send(message);
            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setCode(code);
            verificationCode.setEmail(email);
            verificationCode.setIsValid(true);
            verificationCodeRepository.save(verificationCode);
        } catch (Exception e) {
            throw new BusinessException(TransactionCode.EMAIL_SEND_FAILED);
        }
    }

    private Gamer checkGamer(String userId) {
        Optional<Gamer> userOptional = gamerRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.USER_NOT_FOUND);
        }
        Gamer gamer = userOptional.get();
        if (!gamer.getIsVerified()) {
            throw new BusinessException(TransactionCode.USER_NOT_VERIFIED);
        }
        return gamer;
    }

    private void mapAndSetUserGames(Gamer gamer, List<String> favGames) {
        favGames.forEach(game -> {
            Optional<Games> gameOptional = gamesRepository.findByGameName(game);
            if (gameOptional.isPresent()) {
                Games game1 = gameOptional.get();
                gamer.getLikedgames().add(game1);
            }
        });
    }

    private void mapAndSetKeywords(Gamer gamer, List<String> keyWords) {
        keyWords.forEach(keyword -> {
            Optional<Keywords> keywordOptional = keywordsRepository.findByKeywordName(keyword);
            if (keywordOptional.isPresent()) {
                Keywords keyword1 = keywordOptional.get();
                gamer.getKeywords().add(keyword1);
            }
        });
    }
}
