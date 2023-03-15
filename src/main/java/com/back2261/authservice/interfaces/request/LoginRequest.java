package com.back2261.authservice.interfaces.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String usernameOrEmail;

    @NotBlank(message = "Password field cannot be empty")
    private String password;
}
