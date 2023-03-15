package com.back2261.authservice.interfaces.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TransactionCode {
    DEFAULT_100(100, "Success", HttpStatus.OK),
    EMAIL_EXISTS(101, "Email already exists", HttpStatus.BAD_REQUEST),
    EMAIL_SEND_FAILED(102, "Email send error", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(103, "User not found", HttpStatus.BAD_REQUEST),
    USER_ALREADY_VERIFIED(104, "User already verified", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_NOT_FOUND(105, "Entered code is invalid", HttpStatus.BAD_REQUEST),
    USER_NOT_VERIFIED(106, "User not verified", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTS(107, "Username already taken", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(108, "Entered password is wrong", HttpStatus.BAD_REQUEST),
    USER_NOT_COMPLETED(109, "User details not finished", HttpStatus.BAD_REQUEST),
    TOKEN_INVALID(110, "Token is invalid", HttpStatus.BAD_REQUEST),
    TOKEN_NOT_FOUND(111, "Token not found", HttpStatus.BAD_REQUEST),
    PASSWORD_SAME(112, "New password cannot be same as old password", HttpStatus.BAD_REQUEST),
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
