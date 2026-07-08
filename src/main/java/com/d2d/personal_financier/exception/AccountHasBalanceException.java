package com.d2d.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class AccountHasBalanceException extends BaseException {

    public AccountHasBalanceException() {
        super("Account cannot be deleted because it has a non-zero balance.", HttpStatus.CONFLICT);
    }
}
