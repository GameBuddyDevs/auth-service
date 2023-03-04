package com.back2261.Gamebuddy.domain.service;

import com.back2261.Gamebuddy.interfaces.request.RegisterRequest;
import com.back2261.Gamebuddy.interfaces.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest registerRequest);
}
