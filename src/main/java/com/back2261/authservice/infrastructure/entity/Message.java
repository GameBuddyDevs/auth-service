package com.back2261.authservice.infrastructure.entity;

import com.back2261.authservice.util.MessageStatus;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "messages")
public class Message implements Serializable {

    @Id
    private String id;

    private String chatId;
    private String sender;
    private String receiver;
    private String senderName;
    private String receiverName;
    private String messageBody;
    private Date date;
    private MessageStatus status;
    private Boolean isReported = false;
}
