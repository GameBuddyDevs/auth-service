package com.back2261.authservice.domain.service;

import com.back2261.authservice.interfaces.request.*;
import com.back2261.authservice.interfaces.response.*;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);

    RegisterResponse register(RegisterRequest registerRequest);

    VerifyResponse verifyCode(VerifyRequest verifyRequest);

    DefaultMessageResponse changePwd(ChangePwdRequest changePwdRequest);

    DefaultMessageResponse setUsername(UsernameRequest usernameRequest);

    DefaultMessageResponse details(DetailsRequest detailsRequest);

    TokenResponse validateToken(String token);

    DefaultMessageResponse sendVerificationEmail(SendCodeRequest sendCodeRequest);
}
