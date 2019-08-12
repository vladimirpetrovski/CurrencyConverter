package com.vladimirpetrovski.currencyconverter.domain.model

data class CalculatedRate(
    val currency: String = "EUR",
    val description: String = "Euro",
    val flagUrl: String = "https://www.countryflags.io/EU/flat/64.png",
    val amount: Double = 1.0,
    val isEnabled: Boolean = true
)