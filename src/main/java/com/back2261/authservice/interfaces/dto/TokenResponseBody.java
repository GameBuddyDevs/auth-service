package com.back2261.authservice.interfaces.dto;

import com.back2261.authservice.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponseBody extends BaseModel {
    private String username;
    private Boolean isValid;
}
