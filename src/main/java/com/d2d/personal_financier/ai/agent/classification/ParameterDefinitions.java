package com.d2d.personal_financier.ai.agent.classification;

public final class ParameterDefinitions {

    private ParameterDefinitions() {
    }

    public static final String PARAMETERS = """
        Parameter Extraction Rules:

        Extract parameters only when they are explicitly mentioned.

        Currency Parameters:

        - fromCurrency
        - toCurrency
        - source

        Supported currencies:

        - UAH
        - USD
        - EUR

        Supported exchange rate providers:

        - MONOBANK
        - PRIVATBANK
        - NBU

        Currency examples:

        "Dollar" -> USD
        "US Dollar" -> USD
        "USD" -> USD

        "Euro" -> EUR
        "EUR" -> EUR

        "Hryvnia" -> UAH
        "UAH" -> UAH

        Provider examples:

        "Monobank" -> MONOBANK
        "Mono" -> MONOBANK

        "PrivatBank" -> PRIVATBANK
        "Privat" -> PRIVATBANK

        "NBU" -> NBU
        "National Bank of Ukraine" -> NBU
        "Національний банк України" -> NBU
        "Национальный банк Украины" -> NBU

        If a parameter is not specified,
        return null for that parameter.
        """;

}
