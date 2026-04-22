package com.d2d.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class RefreshTokenExpiredException extends BaseException {

    public RefreshTokenExpiredException() {
        super("Refresh token has expired", HttpStatus.UNAUTHORIZED);
    }
}
