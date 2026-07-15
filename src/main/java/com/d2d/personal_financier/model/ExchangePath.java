package com.d2d.personal_financier.model;

import java.util.List;

public record ExchangePath(
    List<ExchangeStep> steps
) {
}
