package com.d2d.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class InvalidVerificationTokenException extends BaseException {

    public InvalidVerificationTokenException() {
        super("Invalid email verification token", HttpStatus.BAD_REQUEST);
    }
}
