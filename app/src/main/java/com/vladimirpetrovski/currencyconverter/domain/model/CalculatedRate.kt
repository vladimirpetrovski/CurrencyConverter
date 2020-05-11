package com.vladimirpetrovski.currencyconverter.domain.model

import java.math.BigDecimal

data class CalculatedRate(
    val currency: String = "EUR",
    val description: String = "Euro",
    val flagUrl: String = "https://www.countryflags.io/EU/flat/64.png",
    val amount: BigDecimal = 1.0.toBigDecimal(),
    val isEnabled: Boolean = true
)