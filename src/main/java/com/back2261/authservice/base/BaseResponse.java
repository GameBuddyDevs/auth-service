package com.back2261.authservice.base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse<T extends BaseModel> {
    private BaseBody<T> body;

    private Status status;
}
