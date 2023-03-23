package com.back2261.authservice.application.controller;

import com.back2261.authservice.domain.service.AuthService;
import com.back2261.authservice.interfaces.request.*;
import com.back2261.authservice.interfaces.response.*;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
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

    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_MESSAGE = "Authorization field cannot be empty";

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.login(loginRequest), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return new ResponseEntity<>(authService.register(registerRequest), HttpStatus.CREATED);
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verifyCode(@Valid @RequestBody VerifyRequest registerRequest) {
        return new ResponseEntity<>(authService.verifyCode(registerRequest), HttpStatus.OK);
    }

    @PostMapping("/username")
    public ResponseEntity<DefaultMessageResponse> setUsername(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody UsernameRequest usernameRequest) {
        return new ResponseEntity<>(authService.setUsername(token.substring(7), usernameRequest), HttpStatus.OK);
    }

    @PostMapping("/details")
    public ResponseEntity<DefaultMessageResponse> details(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody DetailsRequest detailsRequest) {
        return new ResponseEntity<>(authService.details(token.substring(7), detailsRequest), HttpStatus.OK);
    }

    @PostMapping("/validateToken")
    public ResponseEntity<TokenResponse> validateToken(
            @Valid @RequestHeader("Authorization") @NotBlank(message = AUTH_MESSAGE) String token) {
        return new ResponseEntity<>(authService.validateToken(token.substring(7)), HttpStatus.OK);
    }

    @PostMapping("/sendCode")
    public ResponseEntity<DefaultMessageResponse> sendCode(@Valid @RequestBody SendCodeRequest sendCodeRequest) {
        return new ResponseEntity<>(authService.sendVerificationEmail(sendCodeRequest), HttpStatus.OK);
    }

    @PutMapping("/change/pwd")
    public ResponseEntity<DefaultMessageResponse> changePwd(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody ChangePwdRequest changePwdRequest) {
        return new ResponseEntity<>(authService.changePwd(token.substring(7), changePwdRequest), HttpStatus.OK);
    }
}
