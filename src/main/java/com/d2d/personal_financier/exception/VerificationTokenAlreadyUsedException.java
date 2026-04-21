package com.d2d.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class VerificationTokenAlreadyUsedException extends BaseException {

    public VerificationTokenAlreadyUsedException() {
        super("Email verification token has already been used", HttpStatus.CONFLICT);
    }
}
