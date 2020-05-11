package com.vladimirpetrovski.currencyconverter.domain

import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.model.Rate
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateRatesHelperTest {

    private val calculateRatesHelper = CalculateRatesHelper()

    @Test
    fun `initialCalculate should create EUR rate as first item`() {
        // Given
        val initialAmount = 1.0.toBigDecimal()
        val rates = listOf(
            Rate(
                currency = "HRK",
                rate = 7.43.toBigDecimal()
            ), Rate(
                currency = "USD",
                rate = 1.16.toBigDecimal()
            ), Rate(
                currency = "AUD",
                rate = 1.61.toBigDecimal()
            )
        )

        // When
        val initialList = calculateRatesHelper.initialCalculate(initialAmount, rates)

        // Then
        val calculatedRate = initialList[0]
        assertEquals("EUR", calculatedRate.currency)
        assertEquals("Euro", calculatedRate.description)
        assertEquals("https://www.countryflags.io/EU/flat/64.png", calculatedRate.flagUrl)

        assert(initialList.size == 4)
    }

    @Test
    fun `initialCalculate should return calculated amount list from the given latest rates`() {
        // Given
        val initalAmount = 1.toBigDecimal()
        val rates = listOf(
            Rate(
                currency = "HRK",
                rate = 7.43.toBigDecimal()
            ), Rate(
                currency = "USD",
                rate = 1.16.toBigDecimal()
            ), Rate(
                currency = "AUD",
                rate = 1.61.toBigDecimal()
            )
        )

        // When
        val initialList = calculateRatesHelper.initialCalculate(initalAmount, rates)

        // Then
        val expected = listOf(
            CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 1.toBigDecimal(),
                isEnabled = true
            ), CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 7.43.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 1.16.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 1.61.toBigDecimal(),
                isEnabled = false
            )
        )

        assertEquals(expected, initialList)
    }

    @Test
    fun `calculate should return calculated amount list from the given latest rates`() {
        // Given
        val givenAmount = 2.toBigDecimal()
        val initialRates = listOf(
            CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 1.0.toBigDecimal(),
                isEnabled = true
            ), CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 7.43.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 1.16.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 1.61.toBigDecimal(),
                isEnabled = false
            )
        )

        val rates = listOf(
            Rate(
                currency = "HRK",
                rate = 7.43.toBigDecimal()
            ), Rate(
                currency = "USD",
                rate = 1.16.toBigDecimal()
            ), Rate(
                currency = "AUD",
                rate = 1.61.toBigDecimal()
            )
        )

        // When
        val initialList = calculateRatesHelper.calculate(initialRates, givenAmount, rates)

        // Then
        val expected = listOf(
            CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 2.toBigDecimal(),
                isEnabled = true
            ), CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 14.86.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 2.32.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 3.22.toBigDecimal(),
                isEnabled = false
            )
        )

        assertEquals(expected, initialList)
    }

    @Test
    fun `get display name from EUR should return Euro`() {
        // Given
        val currency = "EUR"

        // When
        val displayName = calculateRatesHelper.getDisplayName(currency)

        // Then
        assertEquals("Euro", displayName)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `get display name from ABC should throw IllegalArgumentException`() {
        // Given
        val currency = "ABC"

        // When
        calculateRatesHelper.getDisplayName(currency)
    }

    @Test
    fun `get flag from EUR should return EU flag url`() {
        // Given
        val currency = "EUR"

        // When
        val flagUrl = calculateRatesHelper.getFlagUrl("EUR")

        // Then
        assertEquals("https://www.countryflags.io/EU/flat/64.png", flagUrl)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `get flag from EURR should throw IllegalArgumentException`() {
        // Given
        val currency = "EURR"

        // When
        calculateRatesHelper.getFlagUrl(currency)
    }
}