package com.back2261.authservice.interfaces.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendCodeRequest {
    @NotBlank(message = "Email field cannot be empty")
    @Email(message = "Email is not valid")
    private String email;

    @NotNull(message = "isRegister field cannot be empty")
    private Boolean isRegister;
}
