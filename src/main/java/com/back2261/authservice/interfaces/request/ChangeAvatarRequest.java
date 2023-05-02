package com.back2261.authservice.interfaces.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeAvatarRequest {
    @NotBlank(message = "Avatar ID cannot be empty")
    private String avatarId;
}
