package com.D2D.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyTakenException extends BaseException {

    public UsernameAlreadyTakenException(String username) {
        super("Username already taken: " + username, HttpStatus.CONFLICT);
    }
}
