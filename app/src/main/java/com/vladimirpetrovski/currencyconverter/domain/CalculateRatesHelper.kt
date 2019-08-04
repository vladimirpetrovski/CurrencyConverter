package com.vladimirpetrovski.currencyconverter.domain

import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.model.Rate
import java.util.Currency

object CalculateRatesHelper {

    fun initialCalculate(amount: Double, latestRates: List<Rate>): List<CalculatedRate> {
        val list = latestRates.map { rate ->
            return@map CalculatedRate(
                currency = rate.currency,
                description = getDisplayName(rate.currency),
                flagUrl = getFlagUrl(rate.currency),
                amount = amount * rate.rate,
                isEnabled = false
            )
        }.toMutableList() // calculated rate

        list.add(
            0, CalculatedRate(
                amount = amount
            )
        ) // selected rate

        return list
    }

    fun calculate(
        oldCalculatedRates: List<CalculatedRate>,
        amount: Double,
        latestRates: List<Rate>
    ): List<CalculatedRate> {
        return oldCalculatedRates.map { calculatedRate ->

            val rate = latestRates.find {
                it.currency == calculatedRate.currency
            } ?: return@map calculatedRate.copy(
                amount = amount,
                isEnabled = true
            ) // return selected rate

            return@map calculatedRate.copy(
                amount = amount * rate.rate,
                isEnabled = false
            ) // return calculated rate
        }
    }

    fun getDisplayName(currency: String): String {
        val currencyInstance = Currency.getInstance(currency)
        return currencyInstance.displayName
    }

    fun getFlagUrl(currency: String): String {
        val currencyInstance = Currency.getInstance(currency) // validate currency code
        val countryCode = currencyInstance.currencyCode.substring(0, 2)
        return "https://www.countryflags.io/$countryCode/flat/64.png"
    }
}