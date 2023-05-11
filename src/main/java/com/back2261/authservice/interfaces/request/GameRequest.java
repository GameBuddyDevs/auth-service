package com.back2261.authservice.interfaces.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameRequest {
    @NotBlank(message = "Game name cannot be empty")
    private String gameName;

    @NotBlank(message = "Game description cannot be empty")
    private String gameDescription;

    private String gameIcon;

    @NotBlank(message = "Game category cannot be empty")
    private String category;

    @NotNull(message = "Game rating cannot be empty")
    private Float rating;
}
