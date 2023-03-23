package com.back2261.authservice.exception;

import io.github.GameBuddyDevs.backendlibrary.enums.TransactionCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final TransactionCode transactionCode;

    public BusinessException(TransactionCode transactionCode) {
        this.transactionCode = transactionCode;
    }

    @Override
    public String getMessage() {

        return "Error:" + transactionCode.name() + "\n" + "ErrorId:" + transactionCode.getId() + "\n" + "ErrorCode:"
                + transactionCode.getCode();
    }
}
