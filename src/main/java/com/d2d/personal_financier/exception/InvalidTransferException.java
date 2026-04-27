package com.d2d.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class InvalidTransferException extends BaseException {

    public InvalidTransferException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
