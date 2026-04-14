package com.D2D.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends BaseException {

    public InsufficientBalanceException(Long accountId) {
        super("Insufficient balance for account id: " + accountId, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
