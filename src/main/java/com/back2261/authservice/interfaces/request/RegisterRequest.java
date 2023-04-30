package com.back2261.authservice.interfaces.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @Email
    @NotBlank(message = "Email field cannot be empty")
    private String email;

    @NotBlank(message = "Password field cannot be empty")
    private String password;

    @NotBlank(message = "Firebase token field cannot be empty")
    private String fcmToken;
}
