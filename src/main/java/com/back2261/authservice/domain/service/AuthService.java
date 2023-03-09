package com.back2261.authservice.domain.service;

import com.back2261.authservice.interfaces.request.*;
import com.back2261.authservice.interfaces.response.DefaultMessageResponse;
import com.back2261.authservice.interfaces.response.LoginResponse;
import com.back2261.authservice.interfaces.response.RegisterResponse;
import com.back2261.authservice.interfaces.response.TokenResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);

    RegisterResponse register(RegisterRequest registerRequest);

    DefaultMessageResponse verifyCode(VerifyRequest verifyRequest);

    DefaultMessageResponse setUsername(UsernameRequest usernameRequest);

    DefaultMessageResponse details(DetailsRequest detailsRequest);

    TokenResponse validateToken(String token);
}
