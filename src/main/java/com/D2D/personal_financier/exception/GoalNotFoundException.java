package com.D2D.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class GoalNotFoundException extends BaseException {

    public GoalNotFoundException(Long id) {
        super("Goal not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
