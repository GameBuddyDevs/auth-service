package com.back2261.authservice.infrastructure.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "messages")
public class Message implements Serializable {
    @Id
    private String id;

    private String sender;
    private String receiver;
    private String messageBody;
    private Date date;
    private Boolean isReported;
}
