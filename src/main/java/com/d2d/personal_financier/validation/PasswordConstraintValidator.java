package com.d2d.personal_financier.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isBlank()) {
            return false;
        }

        return PASSWORD_PATTERN.matcher(value).matches();
    }
}
