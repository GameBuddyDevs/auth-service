package com.back2261.authservice.interfaces.dto;

import com.back2261.authservice.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DefaultMessageBody extends BaseModel {
    private String message;
}
