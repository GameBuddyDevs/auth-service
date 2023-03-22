package com.back2261.authservice.interfaces.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Username or email field cannot be empty")
    private String usernameOrEmail;

    @NotBlank(message = "Password field cannot be empty")
    private String password;
}
