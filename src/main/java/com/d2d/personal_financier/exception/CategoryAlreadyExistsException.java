package com.d2d.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class CategoryAlreadyExistsException extends BaseException {

    public CategoryAlreadyExistsException(String name) {
        super("Category already exists with name: " + name, HttpStatus.CONFLICT);
    }
}
