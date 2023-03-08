package com.back2261.authservice.domain.service;

import com.back2261.authservice.base.BaseBody;
import com.back2261.authservice.base.Status;
import com.back2261.authservice.exception.BusinessException;
import com.back2261.authservice.infrastructure.entity.Games;
import com.back2261.authservice.infrastructure.entity.Keywords;
import com.back2261.authservice.infrastructure.entity.User;
import com.back2261.authservice.infrastructure.entity.VerificationCode;
import com.back2261.authservice.infrastructure.repository.GamesRepository;
import com.back2261.authservice.infrastructure.repository.KeywordsRepository;
import com.back2261.authservice.infrastructure.repository.UserRepository;
import com.back2261.authservice.infrastructure.repository.VerificationCodeRepository;
import com.back2261.authservice.interfaces.dto.DefaultMessageBody;
import com.back2261.authservice.interfaces.dto.RegisterResponseBody;
import com.back2261.authservice.interfaces.enums.TransactionCode;
import com.back2261.authservice.interfaces.request.DetailsRequest;
import com.back2261.authservice.interfaces.request.RegisterRequest;
import com.back2261.authservice.interfaces.request.UsernameRequest;
import com.back2261.authservice.interfaces.request.VerifyRequest;
import com.back2261.authservice.interfaces.response.DefaultMessageResponse;
import com.back2261.authservice.interfaces.response.RegisterResponse;
import com.back2261.authservice.util.Constants;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final GamesRepository gamesRepository;
    private final KeywordsRepository keywordsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new BusinessException(TransactionCode.EMAIL_EXISTS);
        }
        User newUser = new User();
        newUser.setUserId(UUID.randomUUID().toString());
        newUser.setEmail(email);
        String encodedPassword = passwordEncoder.encode(password);
        newUser.setPwd(encodedPassword);
        userRepository.save(newUser);
        sendVerificationEmail(email);
        RegisterResponse registerResponse = new RegisterResponse();
        RegisterResponseBody registerResponseBody = new RegisterResponseBody();
        registerResponseBody.setUserId(newUser.getUserId());
        registerResponse.setBody(new BaseBody<>(registerResponseBody));
        registerResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return registerResponse;
    }

    @Override
    public DefaultMessageResponse verifyCode(VerifyRequest verifyRequest) {
        String userId = verifyRequest.getUserId();
        Integer code = verifyRequest.getVerificationCode();
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.USER_NOT_FOUND);
        }
        User user = userOptional.get();
        if (user.getIsVerified()) {
            throw new BusinessException(TransactionCode.USER_ALREADY_VERIFIED);
        }
        String email = user.getEmail();
        Optional<VerificationCode> verificationCodeOptional =
                verificationCodeRepository.findByEmailAndCodeAndIsValidTrue(email, code);
        if (verificationCodeOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.VERIFICATION_CODE_NOT_FOUND);
        }
        VerificationCode verificationCode = verificationCodeOptional.get();
        verificationCode.setIsValid(false);
        verificationCodeRepository.save(verificationCode);

        user.setIsVerified(true);
        userRepository.save(user);
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
        User user = checkUser(userId);
        Optional<User> usernameCheckOptional = userRepository.findByUsername(username);

        if (usernameCheckOptional.isPresent()) {
            User usernameCheck = usernameCheckOptional.get();
            if (!Objects.equals(usernameCheck.getUserId(), user.getUserId())) {
                throw new BusinessException(TransactionCode.USERNAME_EXISTS);
            }
        }

        if (!Objects.equals(user.getUsername(), username)) {
            user.setUsername(username);
            userRepository.save(user);
        }

        DefaultMessageResponse verifyResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Username set successfully");
        verifyResponse.setBody(new BaseBody<>(body));
        verifyResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return verifyResponse;
    }

    public DefaultMessageResponse details(DetailsRequest detailsRequest) {
        String userId = detailsRequest.getUserId();
        Integer age = detailsRequest.getAge();
        String country = detailsRequest.getCountry();
        byte[] avatar = detailsRequest.getAvatar();
        List<String> keyWords = detailsRequest.getKeywords();
        List<String> favGames = detailsRequest.getFavoriteGames();

        User user = checkUser(userId);
        user.setAge(age);
        user.setCountry(country);
        user.setAvatar(avatar);
        mapAndSetKeywords(user, keyWords);
        mapAndSetUserGames(user, favGames);
        user.setIsRegistered(true);
        userRepository.save(user);

        DefaultMessageResponse verifyResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("User details fetched successfully");
        verifyResponse.setBody(new BaseBody<>(body));
        verifyResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return verifyResponse;
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

    private User checkUser(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.USER_NOT_FOUND);
        }
        User user = userOptional.get();
        if (!user.getIsVerified()) {
            throw new BusinessException(TransactionCode.USER_NOT_VERIFIED);
        }
        return user;
    }

    private void mapAndSetUserGames(User user, List<String> favGames) {
        favGames.forEach(game -> {
            Optional<Games> gameOptional = gamesRepository.findByGameName(game);
            if (gameOptional.isPresent()) {
                Games game1 = gameOptional.get();
                user.getLikedgames().add(game1);
            }
        });
    }

    private void mapAndSetKeywords(User user, List<String> keyWords) {
        keyWords.forEach(keyword -> {
            Optional<Keywords> keywordOptional = keywordsRepository.findByKeywordName(keyword);
            if (keywordOptional.isPresent()) {
                Keywords keyword1 = keywordOptional.get();
                user.getKeywords().add(keyword1);
            }
        });
    }
}
