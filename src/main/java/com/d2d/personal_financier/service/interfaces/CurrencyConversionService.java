package com.d2d.personal_financier.service.interfaces;

import com.d2d.personal_financier.dto.currency_dto.CurrencyConversionRequestDto;
import com.d2d.personal_financier.dto.currency_dto.CurrencyConversionResponseDto;

public interface CurrencyConversionService {

    CurrencyConversionResponseDto convert(
        CurrencyConversionRequestDto request
    );

}
