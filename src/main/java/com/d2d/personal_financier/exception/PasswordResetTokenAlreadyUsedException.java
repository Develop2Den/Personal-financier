package com.d2d.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class PasswordResetTokenAlreadyUsedException extends BaseException {

    public PasswordResetTokenAlreadyUsedException() {
        super("Password reset token has already been used", HttpStatus.CONFLICT);
    }
}
