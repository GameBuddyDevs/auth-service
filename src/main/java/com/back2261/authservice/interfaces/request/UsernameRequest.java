package com.back2261.authservice.interfaces.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsernameRequest {
    @NotBlank
    private String userId;

    @NotBlank
    private String username;
}
