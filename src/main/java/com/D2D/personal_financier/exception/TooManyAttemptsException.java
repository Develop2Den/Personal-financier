package com.D2D.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class TooManyAttemptsException extends BaseException {

    public TooManyAttemptsException() {
        super("Too many login attempts. Try again later.", HttpStatus.TOO_MANY_REQUESTS);
    }
}
