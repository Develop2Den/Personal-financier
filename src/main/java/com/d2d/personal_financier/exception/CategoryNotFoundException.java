package com.d2d.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends BaseException {

    public CategoryNotFoundException(Long id) {
        super("Category not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
