package com.back2261.Gamebuddy.interfaces.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TransactionCode {
    DEFAULT_100(100, "Success", HttpStatus.OK),
    EMAIL_EXISTS(101, "Email already exists", HttpStatus.BAD_REQUEST),
    EMAIL_SEND_FAILED(102, "Email send error", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(103, "User not found", HttpStatus.BAD_REQUEST),
    DB_ERROR(-99, "Database Error", HttpStatus.BAD_REQUEST);

    private final int id;
    private final String code;
    private final HttpStatus httpStatus;

    TransactionCode(int id, String code, HttpStatus httpStatus) {
        this.id = id;
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
