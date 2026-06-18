package com.d2d.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class AccountAlreadyExistsException extends BaseException {

    public AccountAlreadyExistsException(String name) {
        super("Account already exists with name: " + name, HttpStatus.CONFLICT);
    }
}
