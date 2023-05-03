package com.back2261.authservice.interfaces.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameRequest {

    private String gameName;
    private String gameDescription;
    private String gameIcon;
    private String category;
    private Float rating;
}
