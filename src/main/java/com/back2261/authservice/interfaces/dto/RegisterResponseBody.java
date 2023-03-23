package com.back2261.authservice.interfaces.dto;

import io.github.GameBuddyDevs.backendlibrary.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseBody extends BaseModel {
    private String userId;
}
