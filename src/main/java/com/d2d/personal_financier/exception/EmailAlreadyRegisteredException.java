package com.d2d.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyRegisteredException extends BaseException {

    public EmailAlreadyRegisteredException(String email) {
        super("Email already registered: " + email, HttpStatus.CONFLICT);
    }
}
