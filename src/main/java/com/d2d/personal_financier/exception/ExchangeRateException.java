package com.d2d.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class ExchangeRateException extends BaseException {

    public ExchangeRateException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }
}
