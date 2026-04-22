package com.d2d.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class PasswordResetTokenExpiredException extends BaseException {

    public PasswordResetTokenExpiredException() {
        super("Password reset token has expired", HttpStatus.GONE);
    }
}
