package com.back2261.authservice.interfaces.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsernameRequest {
    @NotBlank(message = "User ID cannot be empty")
    private String userId;

    @NotBlank(message = "Username cannot be empty")
    private String username;
}
