package com.back2261.authservice.interfaces.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailsRequest {
    @NotBlank
    private String userId;

    @NotNull
    private Integer age;

    @NotBlank
    private String country;

    private byte[] avatar;

    @Size(max = 1)
    private String gender;

    @Size(min = 1)
    private List<String> favoriteGames;

    @Size(min = 3)
    private List<String> keywords;
}
