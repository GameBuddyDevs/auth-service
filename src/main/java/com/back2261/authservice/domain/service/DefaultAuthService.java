package com.back2261.authservice.domain.service;

import com.back2261.authservice.base.BaseBody;
import com.back2261.authservice.base.Status;
import com.back2261.authservice.exception.BusinessException;
import com.back2261.authservice.infrastructure.entity.User;
import com.back2261.authservice.infrastructure.entity.VerificationCode;
import com.back2261.authservice.infrastructure.repository.UserRepository;
import com.back2261.authservice.infrastructure.repository.VerificationCodeRepository;
import com.back2261.authservice.interfaces.dto.RegisterResponseBody;
import com.back2261.authservice.interfaces.enums.TransactionCode;
import com.back2261.authservice.interfaces.request.RegisterRequest;
import com.back2261.authservice.interfaces.response.RegisterResponse;
import com.back2261.authservice.util.Constants;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
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

    private void sendVerificationEmail(String email) {
        List<VerificationCode> verificationCodes = verificationCodeRepository.findAllByEmail(email);
        if (verificationCodes.size() > 0) {
            verificationCodes.forEach(verificationCode -> verificationCode.setIsValid(false));
        }
        Integer code = new Random().nextInt(999999) + 100000;

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
}
