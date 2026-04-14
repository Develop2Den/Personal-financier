package com.D2D.personal_financier.exception;

import org.springframework.http.HttpStatus;

public class BudgetNotFoundException extends BaseException {

    public BudgetNotFoundException(Long id) {
        super("Budget not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
