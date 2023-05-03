package com.back2261.authservice.interfaces.dto;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GamerDto {

    private String userId;
    private String username;
    private String email;
    private String avatar;
    private Date createdDate;
}
