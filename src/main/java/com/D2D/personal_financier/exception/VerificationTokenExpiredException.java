package com.D2D.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class VerificationTokenExpiredException extends BaseException {

    public VerificationTokenExpiredException() {
        super("Email verification token has expired", HttpStatus.GONE);
    }
}
