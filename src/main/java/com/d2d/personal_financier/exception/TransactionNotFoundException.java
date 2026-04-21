package com.d2d.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class TransactionNotFoundException extends BaseException {

    public TransactionNotFoundException(Long id) {
        super("Transaction not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
