package com.D2D.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class EmailNotVerifiedException extends BaseException {

    public EmailNotVerifiedException() {
        super("Please verify your email before logging in", HttpStatus.FORBIDDEN);
    }
}
