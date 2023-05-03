package com.back2261.authservice.interfaces.dto;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {

    private String id;
    private String senderId;
    private String message;
    private Date messageDate;
}
