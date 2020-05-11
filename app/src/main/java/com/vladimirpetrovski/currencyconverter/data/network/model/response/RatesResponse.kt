package com.vladimirpetrovski.currencyconverter.data.network.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class RatesResponse(
    @Json(name = "base") val base: String,
    @Json(name = "rates") val rates: Map<String, BigDecimal>
)
