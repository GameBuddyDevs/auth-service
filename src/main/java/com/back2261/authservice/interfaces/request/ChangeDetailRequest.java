package com.back2261.authservice.interfaces.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeDetailRequest {
    @NotEmpty
    private List<String> gamesOrKeywordsList;
}
