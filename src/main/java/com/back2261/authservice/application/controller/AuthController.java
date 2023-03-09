package com.back2261.authservice.application.controller;

import com.back2261.authservice.base.BaseBody;
import com.back2261.authservice.domain.service.AuthService;
import com.back2261.authservice.exception.BusinessException;
import com.back2261.authservice.interfaces.dto.TokenResponseBody;
import com.back2261.authservice.interfaces.request.*;
import com.back2261.authservice.interfaces.response.DefaultMessageResponse;
import com.back2261.authservice.interfaces.response.LoginResponse;
import com.back2261.authservice.interfaces.response.RegisterResponse;
import com.back2261.authservice.interfaces.response.TokenResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.login(loginRequest), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return new ResponseEntity<>(authService.register(registerRequest), HttpStatus.CREATED);
    }

    @PostMapping("/verify")
    public ResponseEntity<DefaultMessageResponse> verifyCode(@Valid @RequestBody VerifyRequest registerRequest) {
        return new ResponseEntity<>(authService.verifyCode(registerRequest), HttpStatus.OK);
    }

    @PostMapping("/username")
    public ResponseEntity<DefaultMessageResponse> setUsername(@Valid @RequestBody UsernameRequest usernameRequest) {
        return new ResponseEntity<>(authService.setUsername(usernameRequest), HttpStatus.OK);
    }

    @PostMapping("/details")
    public ResponseEntity<DefaultMessageResponse> details(@Valid @RequestBody DetailsRequest detailsRequest) {
        return new ResponseEntity<>(authService.details(detailsRequest), HttpStatus.OK);
    }

    @PostMapping("/validateToken")
    public ResponseEntity<TokenResponse> validateToken(
            @Valid @RequestHeader("Authorization") @NotBlank(message = "Authorization field cannot be empty")
                    String token) {
        try {
            return new ResponseEntity<>(authService.validateToken(token), HttpStatus.OK);
        } catch (BusinessException e) {
            TokenResponse tokenResponse = new TokenResponse();
            TokenResponseBody body = new TokenResponseBody();
            body.setIsValid(false);
            body.setUsername(e.getMessage());
            tokenResponse.setBody(new BaseBody<>(body));
            return new ResponseEntity<>(tokenResponse, HttpStatus.UNAUTHORIZED);
        }

    }
}
