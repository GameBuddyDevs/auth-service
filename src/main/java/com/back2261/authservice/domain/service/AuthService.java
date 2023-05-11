package com.back2261.authservice.domain.service;

import com.back2261.authservice.interfaces.request.*;
import com.back2261.authservice.interfaces.response.*;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);

    RegisterResponse register(RegisterRequest registerRequest);

    VerifyResponse verifyCode(VerifyRequest verifyRequest);

    DefaultMessageResponse changePwd(String token, ChangePwdRequest changePwdRequest);

    DefaultMessageResponse changeAvatar(String token, ChangeAvatarRequest changeAvatarRequest);

    DefaultMessageResponse setUsername(String token, UsernameRequest usernameRequest);

    DefaultMessageResponse details(String token, DetailsRequest detailsRequest);

    TokenResponse validateToken(String token);

    DefaultMessageResponse sendVerificationEmail(SendCodeRequest sendCodeRequest);

    DefaultMessageResponse changeAge(String token, ChangeAgeRequest changeAgeRequest);

    DefaultMessageResponse changeGames(String token, ChangeDetailRequest changeGamesRequest);

    DefaultMessageResponse changeKeywords(String token, ChangeDetailRequest changeKeywordsRequest);
}
