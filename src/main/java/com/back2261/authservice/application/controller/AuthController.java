package com.back2261.authservice.application.controller;

import com.back2261.authservice.domain.service.AuthService;
import com.back2261.authservice.interfaces.request.DetailsRequest;
import com.back2261.authservice.interfaces.request.RegisterRequest;
import com.back2261.authservice.interfaces.request.UsernameRequest;
import com.back2261.authservice.interfaces.request.VerifyRequest;
import com.back2261.authservice.interfaces.response.DefaultMessageResponse;
import com.back2261.authservice.interfaces.response.RegisterResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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
}
