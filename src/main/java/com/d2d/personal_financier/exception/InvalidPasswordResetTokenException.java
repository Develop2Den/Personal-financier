package com.d2d.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class InvalidPasswordResetTokenException extends BaseException {

    public InvalidPasswordResetTokenException() {
        super("Password reset token is invalid", HttpStatus.BAD_REQUEST);
    }
}
