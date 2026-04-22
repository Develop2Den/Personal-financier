package com.d2d.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends BaseException {

    public InvalidRefreshTokenException() {
        super("Refresh token is invalid or has been revoked", HttpStatus.UNAUTHORIZED);
    }
}
