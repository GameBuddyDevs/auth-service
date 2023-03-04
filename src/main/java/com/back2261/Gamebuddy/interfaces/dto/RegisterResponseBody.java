package com.back2261.Gamebuddy.interfaces.dto;

import com.back2261.Gamebuddy.base.BaseModel;
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
