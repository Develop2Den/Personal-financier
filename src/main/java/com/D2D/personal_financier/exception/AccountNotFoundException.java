package com.D2D.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends BaseException {

    public AccountNotFoundException(Long id) {
        super("Account not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
