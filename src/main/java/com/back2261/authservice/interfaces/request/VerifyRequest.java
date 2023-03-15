package com.back2261.authservice.interfaces.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyRequest {
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotNull(message = "Verification code cannot be empty")
    private Integer verificationCode;
}
