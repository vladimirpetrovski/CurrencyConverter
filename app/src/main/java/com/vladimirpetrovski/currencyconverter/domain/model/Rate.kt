package com.vladimirpetrovski.currencyconverter.domain.model

import java.math.BigDecimal

data class Rate(
    val currency: String,
    val rate: BigDecimal
)
